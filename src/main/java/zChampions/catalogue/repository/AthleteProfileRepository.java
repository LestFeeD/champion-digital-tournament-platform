package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.AthleteProfile;

public interface AthleteProfileRepository extends JpaRepository<AthleteProfile, Long> {

    @Query(value = "SELECT COUNT(*) > 0 FROM athlete_profile ap " +
            "JOIN user_athlete_profile uap ON uap.athlete_profile_id = ap.athlete_profile_id " +
            "JOIN users u ON uap.user_id = u.user_id " +
            "WHERE ap.athlete_profile_id = :athleteProfileId AND u.user_id = :userId",
            nativeQuery = true)
    boolean findAthleteProfileByUser(@Param("athleteProfileId") Long athleteProfileId, @Param("userId") Long userId);



}
