package zChampions.catalogue.requestDto.updateRequest;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
public class StandingsResultUpdateDto {
    private Integer numberStandings;
    private Integer score;
    private LocalTime timeParticipant;

}
