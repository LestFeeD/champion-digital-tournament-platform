package zChampions.catalogue.factories;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zChampions.catalogue.controller.UserControllerProfile;
import zChampions.catalogue.entity.AthleteProfile;
import zChampions.catalogue.entity.CoachProfile;
import zChampions.catalogue.entity.JudgeProfile;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.responseDto.AthleteProfileResponseDto;
import zChampions.catalogue.responseDto.CoachProfileUpdateResponseDto;
import zChampions.catalogue.responseDto.JudgeProfileResponseDto;
import zChampions.catalogue.responseDto.ProfileUpdateResponseDto;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProfileUpdateResponseDtoFactory {
    private static final Logger log =  LoggerFactory.getLogger(ProfileUpdateResponseDtoFactory.class);

    public ProfileUpdateResponseDto makeProfileUpdateResponseDto(UserEntity userEntity) {
        Set<AthleteProfileResponseDto> athleteProfiles = new HashSet<>();
        Set<CoachProfileUpdateResponseDto> coachProfiles = new HashSet<>();
        Set<JudgeProfileResponseDto> judgeProfiles = new HashSet<>();


        // Получаем профили пользователя и заполняем соответствующие DTO
        for (AthleteProfile athleteProfile : userEntity.getAthleteProfiles()) {
            log.info("Processing AthleteProfile with ID: {}", athleteProfile.getAthleteProfileId());
            athleteProfiles.add(AthleteProfileResponseDto.builder()
                    .athleteProfileId(athleteProfile.getAthleteProfileId())
                    .typeOfSport(athleteProfile.getTypeOfSport())
                    .experience(athleteProfile.getExperience())
                    .rankAthlete(athleteProfile.getRankAthlete())
                    .disciplines(athleteProfile.getDisciplines())
                    .roleSport(athleteProfile.getRoleSport())
                    .build());
        }

        for (CoachProfile coachProfile : userEntity.getCoachProfiles()) {
            coachProfiles.add(CoachProfileUpdateResponseDto.builder()
                    .coachProfileId(coachProfile.getCoachProfileId())
                    .typeOfSport(coachProfile.getTypeOfSport())
                    .category(coachProfile.getCategory())
                    .specialization(coachProfile.getSpecialization())
                    .hourlyRate(coachProfile.getHourlyRate())
                    .roleSport(coachProfile.getRoleSport())
                    .build());
        }

        for (JudgeProfile judgeProfile : userEntity.getJudgeProfiles()) {
            judgeProfiles.add(JudgeProfileResponseDto.builder()
                    .judgeProfileId(judgeProfile.getJudgeProfileId())
                    .typeOfSport(judgeProfile.getTypeOfSport())
                    .qualification(judgeProfile.getQualification())
                    .roleSport(judgeProfile.getRoleSport())
                    .build());
        }
        return zChampions.catalogue.responseDto.ProfileUpdateResponseDto.builder()
                .athleteProfiles(athleteProfiles)
                .coachProfiles(coachProfiles)
                .judgeProfiles(judgeProfiles)
                .build();
    }
}
