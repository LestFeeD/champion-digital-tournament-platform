package zChampions.catalogue.requestDto.createRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.requestDto.updateRequest.UpdateCoachProfileRequestDto;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProfileRequestDto {

 private CreateAthleteProfileRequestDto athleteProfileDetails;

 private CreateCoachProfileRequestDto coachProfileDetails;

 private CreateJudgeProfileRequestDto judgeProfileDetails;
}
