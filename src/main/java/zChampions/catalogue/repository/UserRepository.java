package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zChampions.catalogue.entity.UserEntity;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByFirstName(String firstName);
    Optional<UserEntity> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE UserEntity  a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Query(value = "SELECT u.user_id, u.city, u.country, u.date_of_birth, u.email, u.enabled, u.first_name, u.gender, " +
            "u.height, u.information, u.last_name, u.locked, u.password, u.patronymic, u.region, u.weight FROM users u " +
            "JOIN event_participants ep ON u.user_id = ep.user_id " +
            "JOIN event e ON ep.event_id = e.event_id " +
            "JOIN user_role_event ure ON ure.user_id = u.user_id " +
            "JOIN user_event_role uer ON ure.event_role_id = uer.event_role_id " +
            "WHERE e.event_id = :eventId AND uer.role = :role", nativeQuery = true)
    Set<UserEntity> findAllByEventIdAndRole(@Param("eventId") Long eventId, @Param("role") String role);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserEventRole u WHERE u.eventRoleId = :eventRoleId")
    void deleteByEventRoleId(@Param("eventRoleId") Long eventRoleId);

    @Query(value = " SELECT u.user_id FROM users u " +
            "JOIN user_token_email us ON us.user_id = u.user_id " +
            "JOIN email_token et ON et.token_id = us.token_id " +
            "  WHERE et.token_id = :tokenId ", nativeQuery = true)
    Long tokenUsers(@Param("tokenId") Long tokenId);

    @Query(value = "SELECT COUNT(*)  FROM users WHERE email = :email", nativeQuery = true)
    Long emailFound(@Param("email") String email);


}
