package zChampions.catalogue.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateResponseDto {
    private Long userId;
    private Set<AthleteProfileResponseDto> athleteProfiles;
    private Set<CoachProfileUpdateResponseDto> coachProfiles;
    private Set<JudgeProfileResponseDto> judgeProfiles;

}
