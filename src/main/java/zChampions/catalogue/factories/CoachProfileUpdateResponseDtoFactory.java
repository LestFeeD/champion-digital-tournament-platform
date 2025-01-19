package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.CoachProfile;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.BasicProfileResponseDto;
import zChampions.catalogue.responseDto.CoachProfileUpdateResponseDto;

@Component
public class CoachProfileUpdateResponseDtoFactory {
    public CoachProfileUpdateResponseDto makeCoachProfileUpdateResponseDto(CoachProfile coachProfile) {
        return CoachProfileUpdateResponseDto.builder()
                .coachProfileId(coachProfile.getCoachProfileId())
                .category(coachProfile.getCategory())
                .specialization(coachProfile.getSpecialization())
                .hourlyRate(coachProfile.getHourlyRate())
                .typeOfSport(coachProfile.getTypeOfSport())
                .userId(coachProfile.getUser().getUserId())
                .roleSport(coachProfile.getRoleSport())
                .build();
    }
}
