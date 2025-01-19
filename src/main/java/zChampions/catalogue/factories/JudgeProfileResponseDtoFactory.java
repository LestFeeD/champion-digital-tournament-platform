package zChampions.catalogue.factories;

import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.JudgeProfile;
import zChampions.catalogue.responseDto.EventResponseDto;
import zChampions.catalogue.responseDto.JudgeProfileResponseDto;

@Component
public class JudgeProfileResponseDtoFactory {
    public JudgeProfileResponseDto makeJudgeProfileResponseDto(JudgeProfile entity) {

        return JudgeProfileResponseDto.builder()
                .judgeProfileId(entity.getJudgeProfileId())
                .qualification(entity.getQualification())
                .typeOfSport(entity.getTypeOfSport())
                .roleSport(entity.getRoleSport())
                .userId(entity.getUser().getUserId())
                .build();
    }
}
