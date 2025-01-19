package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zChampions.catalogue.entity.OrganizationEntity;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Long> {

    List<OrganizationEntity> findByCountry(String country);
    List<OrganizationEntity> findByRegion(String region);
    List<OrganizationEntity> findByTitle(String title);
    List<OrganizationEntity> findByTitleAndRegion(String title, String region);
    List<OrganizationEntity> findByTitleAndCountry(String title, String country);
    List<OrganizationEntity> findByRegionAndCountry(String region, String country);
    List<OrganizationEntity> findByCountryAndRegionAndTitle(String title, String country, String region);
    List<OrganizationEntity> findAllByUsers_UserId(Long userId);
    @Query(value = "SELECT COUNT(*) > 0  FROM  organization o " +
            "JOIN user_organization uo ON uo.organization_id = o.organization_id " +
            "JOIN users u ON uo.user_id = u.user_id " +
            "JOIN user_organization_role uor ON uor.organization_id = o.organization_id " +
            "WHERE o.organization_id = :organizationId AND u.user_id = :userId AND uor.role = 'ORGANIZER'",
            nativeQuery = true)
    int  existsByIdAndUserId(@Param("organizationId") Long organizationId, @Param("userId") Long userId);



}
