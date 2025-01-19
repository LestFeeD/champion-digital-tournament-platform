package zChampions.catalogue.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;

@Data
public class LoginRequest {

    @NotBlank
    @ExtendedEmailValidator
    private String email;
    @NotBlank
    private String password;
}
