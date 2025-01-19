package zChampions.catalogue.requestDto.updateRequest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicProfileEditRequest {


    @Size(min=2, max=30, message = "Размер имени должен быть от 2 до 30")
    private String firstName;

    @Size(min=2, max=30, message = "Размер фамилии должен быть от 2 до 30")
    private String lastName;

    @Size(min=2, max=30, message = "Размер отчества должен быть от 2 до 30")
    private String patronymic;

    private String gender;

    @ExtendedEmailValidator
    @Size(max=70, message = "Вы превысили лимит символов для почты")
    private String email;

    @Size(min=2, max=30, message = "Размер пароля должен быть от 2 до 100")
    private String password;

    private LocalDate dateOfBirth;

    @Max(value= 250, message = "Ваш рост слишком высок :).")
    private Double height;

    @Max(value= 500, message = "Ваш вес избыточен :0.")
    private Double weight;

    private String country;

    private String region;

    private String city;
}
