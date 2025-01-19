package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;

@Data
public class UpdateSignupRequest {

    @NotNull(message = "Значение не должно быть пустым.")
    @Size(min=2, max=30, message = "Размер имени должен быть в пределах от 2 до 30")
    private String firstName;

    @NotNull(message = "Значение не должно быть пустым.")
    @ExtendedEmailValidator
    private String email;
    @NotNull(message = "Значение не должно быть пустым.")
    private String password;
}
