package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.StageStandings;
import zChampions.catalogue.entity.UserOrganizationRole;

public interface StageStandingsRepository extends JpaRepository<StageStandings, Long> {




}
