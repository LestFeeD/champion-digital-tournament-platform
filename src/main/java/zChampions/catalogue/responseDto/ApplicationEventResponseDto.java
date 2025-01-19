package zChampions.catalogue.responseDto;

import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.enumsEntities.Status;

@Builder
@Data
public class ApplicationEventResponseDto {
    private Long applicationId;
    private Long userId;
    private Long eventId;
    private Status status;
}
