package zChampions.catalogue.responseDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import zChampions.catalogue.entity.ConfirmationToken;
import zChampions.catalogue.entity.UserOrganizationRole;
import zChampions.catalogue.entity.UserRoleSystem;
import zChampions.catalogue.exceptions.validation.ExtendedEmailValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class UserResponseDto {
    private Long userId;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String gender;
    private String email;
    private String password;
    private String information;
    private LocalDate dateOfBirth;
    private Double height;
    private Double weight;
    private String country;
    private String region;
    private String city;
    private List<UserOrganizationRole> userOrganizationRoles;
    private Collection<UserRoleSystem> userRoleSystemEntities;
    private Boolean locked = false;
    private Boolean enabled = false;
}
