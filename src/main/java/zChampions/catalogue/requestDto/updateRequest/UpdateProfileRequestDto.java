package zChampions.catalogue.requestDto.updateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.requestDto.createRequest.CreateAthleteProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateJudgeProfileRequestDto;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto {

    private UpdateAthleteProfileRequestDto athleteProfileDetails; // Специфичные данные для спортсменов

    // Поля для обновления профиля тренера
    private UpdateCoachProfileRequestDto coachProfileDetails; // Специфичные данные для тренеров

    // Поля для обновления профиля судьи
    private UpdateJudgeProfileRequestDto judgeProfileDetails;
}
