package zChampions.catalogue.requestDto.createRequest;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateNextRoundStandingsDtoRequest {
    private Long nextStageId;
    private int numberOfTables;
    private LocalDate startMatchTime;
    private Long userId;
    private LocalDate endMatchTime;
    private Integer playerPosition;

}
