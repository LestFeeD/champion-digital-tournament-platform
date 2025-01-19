package zChampions.catalogue.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.responseDto.*;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProfileCreateResponseDtoFactory {

    public ProfileCreateResponseDto makeProfileCreateResponseDto(UserEntity userEntity) {
        Set<AthleteProfileResponseDto> athleteProfiles = new HashSet<>();
        Set<CoachProfileUpdateResponseDto> coachProfiles = new HashSet<>();
        Set<JudgeProfileResponseDto> judgeProfiles = new HashSet<>();

        for (AthleteProfile athleteProfile : userEntity.getAthleteProfiles()) {
            athleteProfiles.add(AthleteProfileResponseDto.builder()
                    .typeOfSport(athleteProfile.getTypeOfSport())
                    .experience(athleteProfile.getExperience())
                    .rankAthlete(athleteProfile.getRankAthlete())
                    .disciplines(athleteProfile.getDisciplines())
                    .roleSport(athleteProfile.getRoleSport())
                    .build());
        }

        for (CoachProfile coachProfile : userEntity.getCoachProfiles()) {
            coachProfiles.add(CoachProfileUpdateResponseDto.builder()
                    .typeOfSport(coachProfile.getTypeOfSport())
                    .category(coachProfile.getCategory())
                    .specialization(coachProfile.getSpecialization())
                    .hourlyRate(coachProfile.getHourlyRate())
                            .roleSport(coachProfile.getRoleSport())
                    .build());
        }

        for (JudgeProfile judgeProfile : userEntity.getJudgeProfiles()) {
            judgeProfiles.add(JudgeProfileResponseDto.builder()
                    .typeOfSport(judgeProfile.getTypeOfSport())
                    .qualification(judgeProfile.getQualification())
                            .roleSport(judgeProfile.getRoleSport())
                    .build());
        }

        return ProfileCreateResponseDto.builder()
                .athleteProfiles(athleteProfiles)
                .coachProfiles(coachProfiles)
                .judgeProfiles(judgeProfiles)
                .build();
    }
}