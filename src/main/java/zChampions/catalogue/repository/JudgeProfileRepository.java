package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.CoachProfile;
import zChampions.catalogue.entity.JudgeProfile;

import java.util.Optional;

public interface JudgeProfileRepository extends JpaRepository<JudgeProfile, Long> {

    @Query(value = "SELECT COUNT(*) > 0 FROM judge_profile jp " +
            "JOIN user_judge_profiles ujp ON ujp.judge_profile_id = jp.judge_profile_id " +
            "JOIN users u ON ujp.user_id = u.user_id " +
            "WHERE jp.judge_profile_id = :judgeProfileId AND u.user_id = :userId",
            nativeQuery = true)
    boolean findJudgeProfileByUser(@Param("judgeProfileId") Long judgeProfileId, @Param("userId") Long userId);
}
