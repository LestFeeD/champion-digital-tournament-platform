package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.ResultStandings;
import zChampions.catalogue.entity.UserOrganizationRole;

public interface ResultStandingsRepository extends JpaRepository<ResultStandings, Long> {

    @Query(value = "SELECT result_standings_id FROM result_standings rs "  +
            "JOIN result_standings_standings rss ON rss.result_standings_id = rs.result_standings_id" +
            "JOIN standings s ON rss.standings_id = s.standings_id " +
            "WHERE s.standings_id = :standingId", nativeQuery = true)
    Long findResultStandings(@Param("standingId") Long standingId);

    @Query(value = "SELECT rs.result_standings_id FROM result_standings rs "  +
            "JOIN result_standings_standings rss ON rss.result_standings_id = rs.result_standings_id " +
            "JOIN standings s ON rss.standings_id = s.standings_id " +
            "WHERE s.standings_id = :standingId AND rs.number_standings = :numberOfTables", nativeQuery = true)
    Long findByUserIdAndStandingId(@Param("numberOfTables") int numberOfTables, @Param("standingId") Long standingId);



}
