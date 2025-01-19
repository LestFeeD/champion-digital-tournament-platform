package zChampions.catalogue.responseDto;

import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class AthleteProfileResponseDto {
    private Long athleteProfileId;
    private RoleSport roleSport;
    private RankAthlete rankAthlete;

    private List<Disciplines> disciplines;

    private Experience experience;
    private KindOfSport typeOfSport;
    private Long userId;
}
