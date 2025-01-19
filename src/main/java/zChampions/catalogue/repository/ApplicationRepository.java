package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import zChampions.catalogue.entity.ApplicationEvent;

public interface ApplicationRepository extends JpaRepository<ApplicationEvent, Long>  {

    boolean existsByEvent_eventIdAndUser_userId(Long eventId, Long userId);

    @Query(value = "SELECT ae.application_id FROM application_event ae " +
            "JOIN event_application ea ON ea.application_id = ae.application_id " +
            "JOIN event e ON e.event_id = ea.event_id " +
            "JOIN event_participants ep ON ep.event_id = e.event_id " +
            "JOIN users u ON u.user_id = ep.user_id " +
            "WHERE u.user_id = :userId AND e.event_id = :eventId", nativeQuery = true)
    Long findApplicationId(Long eventId, Long userId);





}
