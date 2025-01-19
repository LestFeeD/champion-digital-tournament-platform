package zChampions.catalogue.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.enumsEntities.KindOfSport;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EventResponseDto {
    private Long eventId;
    private String title;
    private KindOfSport kindOfSport;
    private String region;
    private String city;
    private LocalDate createdAt;
    private LocalDate  endsAt;
    private String information;
    private String comments;
    private Long organizationIds;
}
