package zChampions.catalogue.responseDto;

import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.TypeOfOrganization;

import java.util.List;

@Data
@Builder
public class AllOrganizationResponseDto {

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
    private List<Long> userIds;
    private List<Long> eventIds;

}
