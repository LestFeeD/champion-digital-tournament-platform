package zChampions.catalogue.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.repository.ApplicationOrganizationRepository;
import zChampions.catalogue.repository.ApplicationRepository;
import zChampions.catalogue.repository.OrganizationRepository;
import zChampions.catalogue.repository.UserOrganizationRoleRepository;

@Component
@AllArgsConstructor
public class UserRoleChecker {
    private final OrganizationRepository organizationEntityRepository;
    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final ApplicationRepository applicationEventRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;



    boolean isOrganizer(Long userId, OrganizationEntity organizations) {
        boolean exists = userOrganizationRoleRepository.existsByUserUserIdAndOrganizationAndRole(userId, organizations, RoleSport.ORGANIZER);
        System.out.println("Role check result: " + exists);
        return exists;

    }

    boolean isOrganizerInEvent(Long userId, Long eventId) {
        boolean exists = userOrganizationRoleRepository.findEventsByUserAndRole(userId, eventId, RoleSport.ORGANIZER);
        System.out.println("Role check result: " + exists);
        return exists;

    }
}
