package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.Qualification;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "judge_profile")
public class JudgeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long judgeProfileId;

    @Enumerated(EnumType.ORDINAL)
    private Qualification qualification;

    @Enumerated(EnumType.STRING)
    private RoleSport roleSport;

    private KindOfSport typeOfSport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;


}
