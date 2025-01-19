package zChampions.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "result_standings")
public class ResultStandings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_standings_id")
    private Long resultStandings_id;

    private Integer score;

    private LocalTime timeParticipant;

    private Integer placeUser;

    private Integer numberStandings;

    private Integer playerPosition;


    @ManyToOne
    @JoinColumn(name = "standings_id")
    private Standings standings;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
