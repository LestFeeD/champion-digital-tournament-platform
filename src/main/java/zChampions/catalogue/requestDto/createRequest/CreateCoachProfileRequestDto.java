package zChampions.catalogue.requestDto.createRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.Category;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Specialization;
import zChampions.catalogue.enumsEntities.KindOfSport;

import java.util.Set;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCoachProfileRequestDto  {

    @NotNull(message = "Выберите роль")
    private RoleSport roleSport;
    private Category category;
    private Specialization specialization;
    private Double hourlyRate;
    @NotNull(message = "Выберите вид спорта")
    private KindOfSport typeOfSport;

}
