package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.Category;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Specialization;
import zChampions.catalogue.enumsEntities.KindOfSport;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coach_profile")
public class CoachProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coachProfileId;

    @Enumerated(EnumType.ORDINAL)
    private Category category;

    @Enumerated(EnumType.ORDINAL)
    private Specialization specialization;

    private Double hourlyRate;

    @Enumerated(EnumType.STRING)
    private RoleSport roleSport;

    private KindOfSport typeOfSport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
