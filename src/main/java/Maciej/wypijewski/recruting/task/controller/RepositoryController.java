package Maciej.wypijewski.recruting.task.controller;

import Maciej.wypijewski.recruting.task.model.GitHubRepository;
import Maciej.wypijewski.recruting.task.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RepositoryController {

    private final GitHubService gitHubService;

    @GetMapping(value = "/github-repos/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GitHubRepository>> getRepositories(@PathVariable String username) throws IOException {
        List<GitHubRepository> repositories = gitHubService.getRepositories(username);
        return new ResponseEntity<>(repositories, HttpStatus.OK);
    }
}

