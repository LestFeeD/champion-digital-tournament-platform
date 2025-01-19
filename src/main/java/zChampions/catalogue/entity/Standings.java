package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "standings")
public class Standings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standings_id")
    private Long standingsId;

    @ManyToMany(mappedBy = "standingsList", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<EventEntity> eventsList;


    @ManyToOne
    @JoinColumn(name = "stage_standings_id")
    private StageStandings stageStandings;

    @ManyToOne
    @JoinColumn(name = "type_standings_id")
    private TypeStandings typeStandings;

    private LocalDate startMatchTime;

    private LocalDate endMatchTime;


    @OneToMany(mappedBy = "standings", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ResultStandings> resultStandingsSet ;
}
