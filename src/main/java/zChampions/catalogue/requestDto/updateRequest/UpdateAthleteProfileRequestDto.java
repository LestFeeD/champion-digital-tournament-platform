package zChampions.catalogue.requestDto.updateRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zChampions.catalogue.enumsEntities.*;

import java.util.List;

@Data
public class UpdateAthleteProfileRequestDto {
    private Long athleteProfileId;
    private Experience experience;
    private RankAthlete rankAthlete;
    private List<Disciplines> disciplines;
    @NotNull(message = "Выберите вид спорта")
    private KindOfSport typeOfSport;
    @NotNull(message = "Выберите роль")
    private RoleSport roleSport;

}
