package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.enumsEntities.RoleSport;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationOrganizationDtoRequest {

    @NotNull(message = "Значение не должно быть пустым.")
    private RoleSport roleSport;

}
