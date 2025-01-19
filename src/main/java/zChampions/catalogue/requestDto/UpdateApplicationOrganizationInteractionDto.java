package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationOrganizationInteractionDto {
    @NotNull
    private Long applicationOrganizationId;
}
