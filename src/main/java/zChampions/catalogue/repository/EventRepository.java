package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.List;


public interface EventRepository extends JpaRepository<EventEntity, Long> {


    List<EventEntity> findByCity(String city);
    List<EventEntity> findByKindOfSport(KindOfSport kindOfSport);
    List<EventEntity> findByCityAndKindOfSport(String city, KindOfSport kindOfSport);
    List<EventEntity> findAllByUserList_UserId(Long userId);



    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.email FROM users u LEFT JOIN event_participants eu ON u.user_id = eu.user_id  JOIN event e ON eu.event_id = e.event_id WHERE e.event_id = :eventId", nativeQuery = true)
    List<Object[]> findUsersWithEvent(@Param("eventId") Long eventId);






}
