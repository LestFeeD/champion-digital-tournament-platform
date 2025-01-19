package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zChampions.catalogue.entity.UserEventRole;
import zChampions.catalogue.entity.UserOrganizationRole;

public interface UserEventRoleRepository extends JpaRepository<UserEventRole, Long> {
    @Query(value = "SELECT uer.event_role_id FROM user_event_role uer " +
            "JOIN event e ON e.event_id = uer.event_id " +
            "JOIN user_role_event ure ON uer.event_role_id = ure.event_role_id " +
            "JOIN users u ON u.user_id = ure.user_id " +
            "WHERE u.user_id = :userId AND e.event_id = :eventId", nativeQuery = true)
    Long findUserEventRoleId(Long eventId, Long userId);
}
