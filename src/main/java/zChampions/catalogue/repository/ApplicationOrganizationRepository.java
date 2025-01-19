package zChampions.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zChampions.catalogue.entity.ApplicationOrganization;

public interface ApplicationOrganizationRepository  extends JpaRepository<ApplicationOrganization, Long> {
    boolean existsByOrganization_organizationIdAndUser_userId(Long organizationId, Long userId);

}
