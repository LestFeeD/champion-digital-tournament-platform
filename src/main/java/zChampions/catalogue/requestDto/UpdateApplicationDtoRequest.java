package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.enumsEntities.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateApplicationDtoRequest {
    @NotNull
    private Long applicationId;
    private Status status;

}
