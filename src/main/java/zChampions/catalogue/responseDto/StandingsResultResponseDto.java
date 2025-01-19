package zChampions.catalogue.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zChampions.catalogue.entity.StageStandings;
import zChampions.catalogue.entity.TypeStandings;
import zChampions.catalogue.enumsEntities.TypeStandingsEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandingsResultResponseDto {

    private String nameStage;

    private Integer numberStandings;

    private TypeStandingsEnum nameType;

    private LocalDate startMatchTime;

    private LocalDate endMatchTime;

    private Integer score;

    private LocalTime timeParticipant;

    private String firstName;

    private String lastName;

}
