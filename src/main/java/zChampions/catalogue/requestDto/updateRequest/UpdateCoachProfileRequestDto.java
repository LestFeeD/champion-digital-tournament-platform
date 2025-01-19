package zChampions.catalogue.requestDto.updateRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.Category;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Specialization;

import java.util.Set;

@Data
public class UpdateCoachProfileRequestDto {
    private Long coachProfileId;
    @NotNull(message = "Выберите роль")
    private RoleSport roleSport;
    private Category category;
    private Specialization specialization;
    private Double hourlyRate;
    @NotNull(message = "Выберите вид спорта")
    private KindOfSport typeOfSport;
}
