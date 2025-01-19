package zChampions.catalogue.requestDto.updateRequest;

import lombok.Data;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.Qualification;
import zChampions.catalogue.enumsEntities.RoleSport;

@Data
public class UpdateJudgeProfileRequestDto {
    private Long judgeProfileId;
    private Qualification qualification;
    private KindOfSport typeOfSport;
    private RoleSport roleSport;

}
