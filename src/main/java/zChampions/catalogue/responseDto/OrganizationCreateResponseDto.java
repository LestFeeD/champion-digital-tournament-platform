package zChampions.catalogue.responseDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.TypeOfOrganization;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;
import zChampions.catalogue.exceptions.validation.PhoneNumberValidator;

import java.util.List;

@Data
@Builder
public class OrganizationCreateResponseDto {
    private Long organizationId;
    private String title;
    private String country;
    private String region;
    private String city;
    private KindOfSport kindOfSport;
    private TypeOfOrganization typeOfOrganization;
    private String email;
    private String phoneNumber;
    private String officialWebsite;
    private String linkWebsite;
    private Long userId;
}
