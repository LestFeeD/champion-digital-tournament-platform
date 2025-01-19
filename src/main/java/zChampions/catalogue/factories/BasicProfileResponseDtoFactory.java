package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.ApplicationEvent;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.ApplicationEventResponseDto;
import zChampions.catalogue.responseDto.BasicProfileResponseDto;

@Component
public class BasicProfileResponseDtoFactory {
    public BasicProfileResponseDto makeBasicProfileResponseDto(UserEntity entity) {
return BasicProfileResponseDto.builder()
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
        .build();
    }

    }
