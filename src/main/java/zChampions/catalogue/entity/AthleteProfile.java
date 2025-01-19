package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "athlete_profile")
public class AthleteProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long athleteProfileId;

    @Enumerated(EnumType.ORDINAL)
    private RankAthlete rankAthlete;

    @ElementCollection(targetClass = Disciplines.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "athlete_disciplines", joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.ORDINAL)
    private List<Disciplines> disciplines;

    @Enumerated(EnumType.ORDINAL)
    private Experience experience;

    private KindOfSport typeOfSport;

    @Enumerated(EnumType.STRING)
    private RoleSport roleSport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
