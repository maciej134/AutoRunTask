package Maciej.wypijewski.recruting.task.service;

import Maciej.wypijewski.recruting.task.model.Branch;
import Maciej.wypijewski.recruting.task.model.GitHubRepository;
import okhttp3.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {

    private OkHttpClient mockClient;

    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        mockClient = mock(OkHttpClient.class);
        gitHubService = Mockito.spy(new GitHubService(mockClient));
    }

    @Test
    public void testGetRepositories_successful() throws IOException {
        // Arrange
        String username = "testuser";
        Request request = new Request.Builder()
                .url("https://api.github.com/users/testuser/repos")
                .header("Accept", "application/json")
                .method("GET", null)
                .build();
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"),
                "[{\"name\":\"testrepo\",\"owner\":{\"login\":\"testuser\"},\"fork\":false}]".getBytes());
        ResponseBody responseBody1 = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"),
                "[]");

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody)
                .message("OK")
                .build();
        doReturn(response).when(gitHubService).execute(eq("https://api.github.com/users/testuser/repos"), any());
        Response response1 = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody1)
                .message("OK")
                .build();
        doReturn(response1).when(gitHubService).execute(eq("https://api.github.com/repos/testuser/testrepo/branches"), any());
//        when(gitHubService.execute(any())).thenReturn(response);

        // Act
        List<GitHubRepository> repositories = gitHubService.getRepositories(username);

        // Assert
        Assertions.assertNotNull(repositories);
        assertEquals(1, repositories.size());
        GitHubRepository repository = repositories.get(0);
        assertEquals("testrepo", repository.getName());
        assertEquals("testuser", repository.getOwnerLogin());
        Assertions.assertTrue(repository.getBranches().isEmpty());
    }

    @Test
    public void testGetRepositories_unsuccessful() throws IOException {
        // Arrange
        String username = "testuser";
        Request request = new Request.Builder()
                .url("https://api.github.com/users/testuser/repos")
                .header("Accept", "application/json")
                .method("GET", null)
                .build();
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("empty")
                .build();
        doReturn(response).when(gitHubService).execute(eq("https://api.github.com/users/testuser/repos"), any());
//        when(mockClient.newCall(request).execute()).thenReturn(response);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gitHubService.getRepositories(username));
    }

    @Test
    public void testGetBranches_successful() throws IOException {
        // Arrange
        String ownerLogin = "testuser";
        String repositoryName = "testrepo";
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/testuser/testrepo/branches")
                .header("Accept", "application/json")
                .build();
        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), "[{\"name\":\"testbranch\",\"commit\":{\"sha\":\"abc123\"}}]".getBytes());
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody)
                .message("OK")
                .build();
        doReturn(response).when(gitHubService).execute(eq("https://api.github.com/repos/testuser/testrepo/branches"), any());
//        when(mockClient.newCall(request).execute()).thenReturn(response);

        // Act
        List<Branch> branches = gitHubService.getBranches(ownerLogin, repositoryName);

        // Assert
        Assertions.assertNotNull(branches);
        assertEquals(1, branches.size());
        Branch branch = branches.get(0);
        assertEquals("testbranch", branch.getName());
        assertEquals("abc123", branch.getLastCommitSha());
    }
}