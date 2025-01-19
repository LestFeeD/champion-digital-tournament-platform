package zChampions.catalogue.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.UserResponseDto;

@Component
@RequiredArgsConstructor
public class UserFactory {

    public UserResponseDto makeUserDto(UserEntity entity) {

        return UserResponseDto.builder()
                .userId(entity.getUserId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .patronymic(entity.getPatronymic())
                .gender(entity.getGender())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .information(entity.getInformation())
                .dateOfBirth(entity.getDateOfBirth())
                .height(entity.getHeight())
                .weight(entity.getWeight())
                .country(entity.getCountry())
                .region(entity.getRegion())
                .city(entity.getCity())
                .userOrganizationRoles(entity.getUserOrganizationRoles())
                .userRoleSystemEntities(entity.getUserRoleSystemEntities())
                .enabled(entity.getEnabled())
                .locked(entity.getLocked())
                .build();

    }
}


