package zChampions.catalogue.requestDto.createRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.enumsEntities.*;

import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAthleteProfileRequestDto {
    private Experience experience;
    private RankAthlete rankAthlete;
    private List<Disciplines> disciplines;
    @NotNull(message = "Выберите вид спорта")
    private KindOfSport typeOfSport;
    @NotNull(message = "Выберите роль")
    private RoleSport roleSport;

}
