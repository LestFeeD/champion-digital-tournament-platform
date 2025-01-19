package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.EventResponseDto;

import java.util.stream.Collectors;

@Component
public class EventFactory {

        public EventResponseDto makeEventDto(EventEntity entity) {

        return EventResponseDto.builder()
                .eventId(entity.getEventId())
                .title(entity.getTitle())
                .kindOfSport(entity.getKindOfSport())
                .region(entity.getRegion())
                .city(entity.getCity())
                .createdAt(entity.getCreatedAt())
                .endsAt(entity.getEndsAt())
                .information(entity.getInformation())
                .comments(entity.getComments())
                .organizationIds(entity.getOrganization().getOrganizationId())
                .build();
    }
}
