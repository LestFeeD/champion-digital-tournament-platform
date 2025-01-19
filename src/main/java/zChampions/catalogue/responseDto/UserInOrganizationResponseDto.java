package zChampions.catalogue.responseDto;

import lombok.*;
import zChampions.catalogue.enumsEntities.RoleSport;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInOrganizationResponseDto {
    private String firstName;
    private RoleSport roleSport;
}
