package zChampions.catalogue.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.AllOrganizationResponseDto;
import zChampions.catalogue.service.OrganizationEntityService;

import java.util.stream.Collectors;

@Component
public class AllOrganizationEntityDtoFactory {

    public AllOrganizationResponseDto makeOrganizations(OrganizationEntity entity) {
        return AllOrganizationResponseDto.builder()
                .organizationId(entity.getOrganizationId())
                .title(entity.getTitle())
                .typeOfOrganization(entity.getTypeOfOrganization())
                .kindOfSport(entity.getKindOfSport())
                .country(entity.getCountry())
                .region(entity.getRegion())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .officialWebsite(entity.getOfficialWebsite())
                .linkWebsite(entity.getLinkWebsite())
                .userIds(entity.getUsers().stream()
                        .map(UserEntity::getUserId)
                        .collect(Collectors.toList()))
                .eventIds(entity.getEvent().stream()
                        .map(EventEntity::getEventId)
                        .collect(Collectors.toList()))
                .build();

    }
}
