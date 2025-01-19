package zChampions.catalogue.responseDto;

import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.Category;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Specialization;

import java.util.Set;

@Data
@Builder
public class CoachProfileUpdateResponseDto {

    private Long coachProfileId;
    private Category category;
    private RoleSport roleSport;
    private Specialization specialization;
    private Double hourlyRate;
    private Long userId;
    private KindOfSport typeOfSport;

}
