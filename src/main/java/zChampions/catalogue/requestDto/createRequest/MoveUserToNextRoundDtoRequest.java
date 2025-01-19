package zChampions.catalogue.requestDto.createRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveUserToNextRoundDtoRequest {

    private Integer numberStandings;
    private Long currentStandingId;
    private Long userId;
    private Integer playerPosition;

}
