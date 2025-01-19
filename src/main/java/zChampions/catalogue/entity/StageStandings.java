package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stage_standings")
public class StageStandings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stage_standings_id")
    private Long stageStandings_id;

    private String nameStage;

    private Integer orderStage;


    @OneToMany(mappedBy = "stageStandings", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Standings> standingsList;

}
