package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.AthleteProfile;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.AthleteProfileResponseDto;
import zChampions.catalogue.responseDto.BasicProfileResponseDto;
@Component
public class AthleteProfileDtoResponseFactory {
    public AthleteProfileResponseDto makeAthleteProfileResponseDto(AthleteProfile athleteProfile) {
        return AthleteProfileResponseDto.builder()
                .athleteProfileId(athleteProfile.getAthleteProfileId())
                .rankAthlete(athleteProfile.getRankAthlete())
                .typeOfSport(athleteProfile.getTypeOfSport())
                .disciplines(athleteProfile.getDisciplines())
                .experience(athleteProfile.getExperience())
                .roleSport(athleteProfile.getRoleSport())
                .userId(athleteProfile.getUser().getUserId())
                .build();

    }
}
