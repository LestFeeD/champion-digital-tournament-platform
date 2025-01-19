package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.entity.UserOrganizationRole;
import zChampions.catalogue.enumsEntities.RoleSport;

import java.util.List;

public interface UserOrganizationRoleRepository extends JpaRepository<UserOrganizationRole, Long> {
    boolean existsByUserUserIdAndOrganizationAndRole(Long userId, OrganizationEntity organization, RoleSport role);
    @Query(value = "SELECT e FROM EventEntity e " +
            "JOIN e.organizationList o " +
            "JOIN o.userOrganizationRoles uor " +
            "JOIN uor.user u " +
            "WHERE uor.role = :roleSport " +
            "AND u.userId = :userId AND e.eventId = :eventId", nativeQuery = true)
    boolean findEventsByUserAndRole(@Param("userId") Long userId,@Param("eventId") Long eventId, @Param("roleSport") RoleSport roleSport);

}
