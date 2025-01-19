package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.Standings;
import zChampions.catalogue.entity.UserOrganizationRole;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.List;
import java.util.Set;

public interface StandingsRepository extends JpaRepository<Standings, Long> {

    @Query(value = "SELECT ts.name_type, rs.score, NULL as time_participant, ss.name_stage, ss.order_stage, u.first_name, u.last_name\n" +
            "FROM standings s " +
            "JOIN type_standings ts ON ts.type_standings_id =  s.type_standings_id " +
            "JOIN stage_standings ss ON ss.stage_standings_id = s.stage_standings_id " +
            "JOIN result_standings_standings rss ON rss.standings_id = s.standings_id " +
            "JOIN result_standings rs ON rss.result_standings_id =  rs.result_standings_id " +
            "JOIN result_standings_user rsu ON rsu.result_standings_id = rs.result_standings_id " +
            "JOIN users u ON rsu.user_id = u.user_id " +
            "JOIN event_standings es ON s.standings_id = es.standings_id " +
            "JOIN event e ON es.event_id = e.event_id " +
            "WHERE e.event_id = :eventId AND ts.name_type = 'OLYMPIC' " +
            "UNION ALL " +
            "SELECT ts.name_type, rs.score, rs.time_participant, ss.name_stage, ss.order_stage, u.first_name, u.last_name\n" +
            "FROM standings s " +
            "JOIN type_standings ts ON ts.type_standings_id =  s.type_standings_id " +
            "JOIN stage_standings ss ON ss.stage_standings_id = s.stage_standings_id " +
            "JOIN result_standings_standings rss ON rss.standings_id = s.standings_id " +
            "JOIN result_standings rs ON rss.result_standings_id =  rs.result_standings_id " +
            "JOIN result_standings_user rsu ON rsu.result_standings_id = rs.result_standings_id " +
            "JOIN users u ON rsu.user_id = u.user_id " +
            "JOIN event_standings es ON s.standings_id = es.standings_id " +
            "JOIN event e ON es.event_id = e.event_id " +
            "WHERE e.event_id = :eventId AND ts.name_type = 'BEST_TIME' ", nativeQuery = true)
    Set<Object[]> findAllAboutStanding(@Param("eventId") Long eventId);


}
