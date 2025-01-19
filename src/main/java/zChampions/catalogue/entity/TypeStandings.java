package zChampions.catalogue.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import zChampions.catalogue.enumsEntities.TypeStandingsEnum;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "type_standings")
public class TypeStandings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_standings_id")
    private Long typeStandingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name_type")
    private TypeStandingsEnum nameType;


    @OneToMany(mappedBy = "typeStandings", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    private List<Standings> standingsList;

}
