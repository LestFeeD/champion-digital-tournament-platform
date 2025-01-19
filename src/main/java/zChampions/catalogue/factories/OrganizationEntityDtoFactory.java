package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.responseDto.OrganizationCreateResponseDto;

@Component
public class OrganizationEntityDtoFactory {

    public OrganizationCreateResponseDto makeOrganization(OrganizationEntity entity) {

        return OrganizationCreateResponseDto.builder()
                .organizationId(entity.getOrganizationId())
                .title(entity.getTitle())
                .typeOfOrganization(entity.getTypeOfOrganization())
                .kindOfSport(entity.getKindOfSport())
                .country(entity.getCountry())
                .region(entity.getRegion())
                .city(entity.getCity())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .officialWebsite(entity.getOfficialWebsite())
                .linkWebsite(entity.getLinkWebsite())
                .userId(entity.getUsers().iterator().next().getUserId())
                .build();

    }
}
