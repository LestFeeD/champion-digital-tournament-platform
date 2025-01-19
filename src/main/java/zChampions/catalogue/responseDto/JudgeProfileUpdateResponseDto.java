package zChampions.catalogue.responseDto;

import lombok.Data;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.Qualification;

@Data
public class JudgeProfileUpdateResponseDto {

    private Long judgeProfileId;
    private Qualification qualification;
    private Long sportProfileUserId;
    private KindOfSport typeOfSport;
}
