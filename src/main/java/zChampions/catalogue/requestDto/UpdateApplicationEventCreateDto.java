package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import zChampions.catalogue.enumsEntities.RoleSport;

@Getter
@Setter
public class UpdateApplicationEventCreateDto {
    @NotNull
    private RoleSport role;

}
