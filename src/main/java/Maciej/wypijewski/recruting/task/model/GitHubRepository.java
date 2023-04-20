package Maciej.wypijewski.recruting.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GitHubRepository {

    private String name;
    private String ownerLogin;
    private List<Branch> branches;
}
