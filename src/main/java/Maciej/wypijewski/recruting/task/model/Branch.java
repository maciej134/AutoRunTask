package Maciej.wypijewski.recruting.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    private String name;
    private String lastCommitSha;
}
