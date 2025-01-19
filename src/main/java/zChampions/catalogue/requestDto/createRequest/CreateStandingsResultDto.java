package zChampions.catalogue.requestDto.createRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStandingsResultDto {
    private String nameStage;

    private Integer orderStage;

    private Integer numberStandings;

    private LocalDate startMatchTime;

    private LocalDate endMatchTime;

    private Long typeStandingsId;
    private Long stageStandingsId;

}
