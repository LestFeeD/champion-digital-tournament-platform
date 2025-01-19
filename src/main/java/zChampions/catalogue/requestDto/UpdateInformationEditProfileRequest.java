package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateInformationEditProfileRequest {
    @Size(max=1000, message = "Вы превысили лимит символов, должно быть меньше 1000.")
    private String information;

}
