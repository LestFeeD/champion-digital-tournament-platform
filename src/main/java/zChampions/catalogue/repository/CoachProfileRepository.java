package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.AthleteProfile;
import zChampions.catalogue.entity.CoachProfile;

import java.util.Optional;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    @Query(value = "SELECT COUNT(*) > 0 FROM coach_profile cp " +
            "JOIN user_coach_profile ucp ON ucp.coach_profile_id = cp.coach_profile_id " +
            "JOIN users u ON ucp.user_id = u.user_id " +
            "WHERE cp.coach_profile_id = :coachProfileId AND u.user_id = :userId",
            nativeQuery = true)
    boolean findCoachProfileByUser(@Param("coachProfileId") Long coachProfileId, @Param("userId") Long userId);
}
