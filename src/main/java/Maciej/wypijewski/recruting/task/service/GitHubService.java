package Maciej.wypijewski.recruting.task.service;

import Maciej.wypijewski.recruting.task.model.Branch;
import Maciej.wypijewski.recruting.task.model.GitHubRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GitHubService {
    private static final String GITHUB_API_URL = "https://api.github.com";

    private final OkHttpClient client;

    public GitHubService(OkHttpClient client) {
        this.client = client;
    }

    public List<GitHubRepository> getRepositories(String username) throws IOException {
        String url = GITHUB_API_URL + "/users/" + username + "/repos";
        try (Response response = execute(url,"GET")) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to retrieve repositories: " + response.body());
            }
            String responseBody = response.body().string();
            List<GitHubRepository> repositories = new ArrayList<>();
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(responseBody, JsonArray.class);
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (!jsonObject.get("fork").getAsBoolean()) {
                    GitHubRepository repository = new GitHubRepository();
                    repository.setName(jsonObject.get("name").getAsString());
                    repository.setOwnerLogin(jsonObject.get("owner").getAsJsonObject().get("login").getAsString());
                    repository.setBranches(getBranches(repository.getOwnerLogin(), repository.getName()));
                    repositories.add(repository);
                }
            }
            return repositories;
        } catch (IOException e) {
            throw new IOException("Failed to retrieve repositories: " + e.getMessage(), e);
        }
    }

    public List<Branch> getBranches(String ownerLogin, String repositoryName) throws IOException {
        String url = GITHUB_API_URL + "/repos/" + ownerLogin + "/" + repositoryName + "/branches";
        Response response = execute(url, "GET");
        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to retrieve branches: " + response.body());
        }

        String responseBody = response.body().string();
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(responseBody, JsonArray.class);
        List<Branch> branches = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Branch branch = new Branch();
            branch.setName(jsonObject.get("name").getAsString());
            branch.setLastCommitSha(jsonObject.get("commit").getAsJsonObject().get("sha").getAsString());
            branches.add(branch);
        }
        return branches;
    }

    protected Response execute(String url, String method) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .method(method, null)
                .build();
        return client.newCall(request).execute();
    }
}

