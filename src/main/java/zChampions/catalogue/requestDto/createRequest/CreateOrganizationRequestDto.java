package zChampions.catalogue.requestDto.createRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.TypeOfOrganization;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;
import zChampions.catalogue.exceptions.validation.PhoneNumberValidator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrganizationRequestDto {

    @NotNull(message = "Значение не должно быть пустым.")
    @Size(min=2, max=30, message = "Размер названия должен быть в пределах от 2 до 30")
    private String title;

    @NotNull(message = "Значение не должно быть пустым.")
    private String country;

    @NotNull(message = "Значение не должно быть пустым.")
    private String region;

    @NotNull(message = "Значение не должно быть пустым.")
    private String city;

    @NotNull(message = "Значение не должно быть пустым.")
    private KindOfSport kindOfSport;

    @NotNull(message = "Значение не должно быть пустым.")
    private TypeOfOrganization typeOfOrganization;

    @NotNull(message = "Значение не должно быть пустым.")
    @ExtendedEmailValidator
    private String email;

    @PhoneNumberValidator
    @NotNull
    private String phoneNumber;

    private String officialWebsite;

}
