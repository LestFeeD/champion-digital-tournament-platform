package zChampions.catalogue.requestDto.createRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.enumsEntities.Qualification;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJudgeProfileRequestDto {

    private Qualification qualification;
    private KindOfSport typeOfSport;
    private RoleSport roleSport;
}
