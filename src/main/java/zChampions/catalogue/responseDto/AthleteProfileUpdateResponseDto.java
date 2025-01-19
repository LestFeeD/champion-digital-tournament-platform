package zChampions.catalogue.responseDto;

import lombok.Data;
import zChampions.catalogue.enumsEntities.Disciplines;
import zChampions.catalogue.enumsEntities.Experience;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RankAthlete;

import java.util.List;

@Data
public class AthleteProfileUpdateResponseDto {
    private Long athleteProfileId;
    private RankAthlete rankAthlete;
    private List<Disciplines> disciplines;
    private Experience experience;
    private Long sportProfileUserId;
    private KindOfSport typeOfSport;
}
