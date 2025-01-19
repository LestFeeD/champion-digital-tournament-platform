package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.exceptions.*;
import zChampions.catalogue.exceptions.IllegalArgumentException;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationDtoRequest;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Status;
import zChampions.catalogue.factories.ApplicationOrganizationFactory;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationInteractionDto;
import zChampions.catalogue.responseDto.ApplicationOrganizationResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ApplicationOrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationOrganizationService.class);


    private final ApplicationOrganizationRepository applicationOrganizationRepository;
    private final ApplicationOrganizationFactory applicationOrganizationFactory;
    private final ControllerHelper controllerHelper;
    private final UserRepository userEntityRepository;
    private final OrganizationRepository organizationEntityRepository;
    private final UserOrganizationRoleRepository userOrganizationRoleRepository;
    private final AuthenticationService authenticationService;
    private final UserRoleChecker userRoleChecker;





    public ApplicationOrganizationResponseDto submitApplicationToTheOrganization(Long organizationId, UpdateApplicationOrganizationDtoRequest applicationOrganizationDtoRequest) throws IllegalArgumentException{

        Long userId = authenticationService.getCurrentUserId();
        logger.info("Submitting application for Organization ID: {}, User ID : {} and role: {}", organizationId,userId, applicationOrganizationDtoRequest.getRoleSport());


        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        OrganizationEntity organization = organizationEntityRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        boolean isApplicationAlreadySubmitted = isApplicationForOrganizationAlreadySubmitted(organizationId, userId);
        if (isApplicationAlreadySubmitted) {
            logger.warn("Attempted to submit duplicate application for organization ID {} by user ID {}.", organizationId, userId);
            throw new DuplicateApplicationException("Заявка для этой организации подана.");
        }

        RoleSport roleSport = applicationOrganizationDtoRequest.getRoleSport();
        if(roleSport == null || !isValidRole(roleSport)){
            logger.warn("An invalid role. Role: {}.", applicationOrganizationDtoRequest.getRoleSport());
            throw new IllegalArgumentException("Недопустимая роль. Допускаются только СПОРТСМЕН и ТРЕНЕР, и СУДЬЯ.");
        }
        logger.info("RoleSport provided: {}", roleSport);

        ApplicationOrganization applicationOrganization = ApplicationOrganization.builder()
                .user(user)
                .roleSport(roleSport)
                .status(Status.PENDING)
                .organization(organization)
                .build();

            applicationOrganization = applicationOrganizationRepository.save(applicationOrganization);

            organization.getApplicationOrganizationSet().add(applicationOrganization);
            organizationEntityRepository.save(organization);

            user.getApplicationOrganizations().add(applicationOrganization);
            userEntityRepository.save(user);
        return applicationOrganizationFactory.makeApplicationOrganizationDto(applicationOrganization);
    }

    public void cancelApplication(Long organizationId, UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws AccessDeniedException {

        Long userId = authenticationService.getCurrentUserId();

        logger.info("Canceling application for Organization ID: {}, User ID: {}", organizationId, userId);


        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);


        ApplicationOrganization applicationOrganization = controllerHelper.getApplicationOrganizationOrThrowException(organizationInteractionDto.getApplicationOrganizationId());

        UserEntity applicant = applicationOrganization.getUser();


        if (!applicant.getUserId().equals(userId)) {
            logger.error("User with ID {} doesn't have permission to cancel application with ID {}", userId, applicationOrganization.getApplicationOrganizationId());
            throw new AccessDeniedException("User does not have permission to cancel this application");
        }

            organization.getApplicationOrganizationSet().removeIf(app -> app.getApplicationOrganizationId().equals(applicationOrganization.getApplicationOrganizationId()));
            organizationEntityRepository.save(organization);

                applicant.getApplicationOrganizations().removeIf(app -> app.getApplicationOrganizationId().equals(applicationOrganization.getApplicationOrganizationId()));
                userEntityRepository.save(applicant);



        if(applicationOrganizationRepository.existsById(organizationInteractionDto.getApplicationOrganizationId())) {
            applicationOrganizationRepository.deleteById(organizationInteractionDto.getApplicationOrganizationId());
            logger.info("Application with ID {} successfully deleted", organizationInteractionDto.getApplicationOrganizationId());
        } else {
            throw new NotFoundException("Application not found with ID: " + organizationInteractionDto.getApplicationOrganizationId());
        }


    }

    public ApplicationOrganizationResponseDto approveApplication(Long organizationId, UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();


        logger.info("Approving application for Organization ID: {}, User ID: {}", organizationId, userId);


        ApplicationOrganization applicationOrganization = controllerHelper.getApplicationOrganizationOrThrowException(organizationInteractionDto.getApplicationOrganizationId());
        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        UserEntity applicant = applicationOrganization.getUser();


        if (!userRoleChecker.isOrganizer(userId, organization)) {
            logger.error("Insufficient user {} rights in the organization {}", userId, organization.getOrganizationId());
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }

        applicationOrganization.setStatus(Status.APPROVED);
        applicationOrganizationRepository.save(applicationOrganization);

            organization.getUsers().add(applicant);
            applicant.getOrganizationEntityList().add(organization);
            userEntityRepository.save(applicant);
            logger.info("Adding applicant with ID {} to organization ID {}", applicant.getUserId(), organization.getOrganizationId());



        organization.getApplicationOrganizationSet().add(applicationOrganization);
        applicationOrganization.setOrganization(organization);
        organizationEntityRepository.save(organization);

        return applicationOrganizationFactory.makeApplicationOrganizationDto(applicationOrganization);
    }

    public void rejectApplication(Long organizationId, UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws RoleNotAllowedException {
        Long userId = authenticationService.getCurrentUserId();

        logger.info("Rejecting application for Organization ID: {}, User ID: {}", organizationId, userId);


        ApplicationOrganization applicationOrganization = controllerHelper.getApplicationOrganizationOrThrowException(organizationInteractionDto.getApplicationOrganizationId());

        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        if (!userRoleChecker.isOrganizer(userId, organization)) {
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }

        if (!applicationOrganization.getStatus().equals(Status.REJECTED)) {
            applicationOrganization.setStatus(Status.REJECTED);
            applicationOrganizationRepository.save(applicationOrganization);
            logger.info("Application with ID {} status changed to REJECTED.", applicationOrganization.getApplicationOrganizationId());
        } else {
            logger.warn("Attempted to change status of application ID {} to REJECTED, but it is already in that status.", applicationOrganization.getApplicationOrganizationId());
        }


    }

    public List<ApplicationOrganizationResponseDto> getSportsmenApplicationsForOrganization(Long organizationId) throws RoleNotAllowedException {
        Long userId = authenticationService.getCurrentUserId();


        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);

        if (!userRoleChecker.isOrganizer(userId, organization)) {
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }


        return organization.getApplicationOrganizationSet().stream()
                .filter(application -> application.getStatus() == Status.PENDING && application.getRoleSport() == RoleSport.SPORTSMAN)
                .map(applicationOrganizationFactory::makeApplicationOrganizationDto)
                .collect(Collectors.toList());
    }

    public List<ApplicationOrganizationResponseDto> getCoachesApplicationsForOrganization(Long organizationId) throws RoleNotAllowedException {
        Long userId = authenticationService.getCurrentUserId();


        OrganizationEntity organization = controllerHelper.getOrganizationOrThrowException(organizationId);


        if (!userRoleChecker.isOrganizer(userId, organization)) {
            throw new RoleNotAllowedException("User is not authorized to view these applications");
        }

        return organization.getApplicationOrganizationSet().stream()
                .filter(application -> application.getStatus() == Status.PENDING  && application.getRoleSport() == RoleSport.COACH)
                .map(applicationOrganizationFactory::makeApplicationOrganizationDto)
                .collect(Collectors.toList());
    }


    private boolean isValidRole(RoleSport roleSport) {
        return roleSport == RoleSport.SPORTSMAN || roleSport == RoleSport.COACH || roleSport == RoleSport.JUDGE;
    }

    public boolean isApplicationForOrganizationAlreadySubmitted(Long eventId, Long userId) {
        return applicationOrganizationRepository.existsByOrganization_organizationIdAndUser_userId(eventId, userId);
    }

}
