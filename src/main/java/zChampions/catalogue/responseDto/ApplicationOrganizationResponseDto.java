package zChampions.catalogue.responseDto;

import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Status;

@Data
@Builder
public class ApplicationOrganizationResponseDto {
    private Long applicationOrganizationId;
    private Long userId;
    private Long organizationId;
    private RoleSport roleSport;
    private Status status;

}
