package zChampions.catalogue.requestDto.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;
import zChampions.catalogue.exceptions.validation.PhoneNumberValidator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrganizationContactSettingsRequestDto {
    @ExtendedEmailValidator
    private String email;

    @PhoneNumberValidator
    private String phoneNumber;

    private String officialWebsite;

}
