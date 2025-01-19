package zChampions.catalogue.responseDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zChampions.catalogue.enumsEntities.*;

import java.util.List;

@Data
public class CreateProfileResponseDto {
    private Experience experience;
    private RankAthlete rankAthlete;
    private List<Disciplines> disciplines;
    private Qualification qualification;
    private Specialization specialization;
    private Category category;
    private Double hourlyRate;
    private RoleSport roleSport;
    private KindOfSport typeOfSport;

}
