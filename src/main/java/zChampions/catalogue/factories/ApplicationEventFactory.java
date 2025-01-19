package zChampions.catalogue.factories;


import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.ApplicationEvent;
import zChampions.catalogue.responseDto.ApplicationEventResponseDto;

@Component
public class ApplicationEventFactory {

    public ApplicationEventResponseDto makeApplicationEventDto(ApplicationEvent entity) {

        return ApplicationEventResponseDto.builder()
                .applicationId(entity.getApplicationId())
                .eventId(entity.getEvent().getEventId())
                .userId(entity.getUser().getUserId())
                .status(entity.getStatus())
                .build();
    }
}
