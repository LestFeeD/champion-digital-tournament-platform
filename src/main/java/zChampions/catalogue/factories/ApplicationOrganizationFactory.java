package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.ApplicationOrganization;
import zChampions.catalogue.responseDto.ApplicationOrganizationResponseDto;

@Component
public class ApplicationOrganizationFactory {

    public ApplicationOrganizationResponseDto makeApplicationOrganizationDto(ApplicationOrganization entity) {

        return ApplicationOrganizationResponseDto.builder()
                .applicationOrganizationId(entity.getApplicationOrganizationId())
                .roleSport(entity.getRoleSport())
                .status(entity.getStatus())
                .userId(entity.getUser().getUserId())
                .organizationId(entity.getOrganization().getOrganizationId())
                .build();
    }
}
