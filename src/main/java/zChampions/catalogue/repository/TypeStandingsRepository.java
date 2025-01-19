package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.Standings;
import zChampions.catalogue.entity.TypeStandings;

public interface TypeStandingsRepository extends JpaRepository<TypeStandings, Long> {
  @Query(value = "SELECT ts.type_standings_id FROM type_standings ts " +
          "JOIN standings s ON ts.type_standings_id = s.type_standings_id " +
          "WHERE s.standings_id = :standingId", nativeQuery = true)
    Long findTypeStandings(@Param("standingId") Long standingId);

  @Query(value = "SELECT type_standings_id FROM type_standings where name_type = 'OLYMPIC'", nativeQuery = true)
  Long findOlympicTypeStandings();

}
