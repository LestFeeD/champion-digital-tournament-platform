package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.exceptions.*;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.UpdateApplicationDtoRequest;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Status;
import zChampions.catalogue.factories.ApplicationEventFactory;
import zChampions.catalogue.requestDto.UpdateApplicationEventCreateDto;
import zChampions.catalogue.responseDto.ApplicationEventResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ApplicationEventService {

    Logger logger = org.slf4j.LoggerFactory.getLogger(ApplicationEventService.class);


    private final ApplicationRepository applicationEventRepository;
    private final ApplicationEventFactory applicationEventFactory;
    private final UserRoleChecker userRoleChecker;
    private final UserRepository userEntityRepository;
    private final EventRepository eventEntityRepository;
    private final ControllerHelper controllerHelper;
    private final AuthenticationService authenticationService;
    private final UserEventRoleRepository userEventRoleRepository;

    public ApplicationEventResponseDto submitApplication(Long eventId, UpdateApplicationEventCreateDto applicationEventCreateDto) throws BadRequestException {

         Long userId = authenticationService.getCurrentUserId();
        logger.info("Submitting application for Event ID: {}, User ID: {}", eventId, userId);

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method submitApplication: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });



        EventEntity event = eventEntityRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        boolean isApplicationAlreadySubmitted = isApplicationForEventAlreadySubmitted(eventId, userId);
        if (isApplicationAlreadySubmitted) {
            logger.warn("Attempted to submit duplicate application for event ID {} by user ID {}.", eventId, userId);
            throw new DuplicateApplicationException("Заявка для этого мероприятия подана.");
        }

        if(!(applicationEventCreateDto.getRole() == RoleSport.SPORTSMAN)) {
            logger.warn("Role Error: Wrong role - {}", applicationEventCreateDto.getRole());
            throw new BadRequestException("Wrong role");
        }

        Set<UserEntity> users = event.getUserList();

        ApplicationEvent applicationEvent = ApplicationEvent.builder()
                .user(user)
                .status(Status.PENDING)
                .role(applicationEventCreateDto.getRole())
                .build();


        applicationEvent.setEvent(event);

        applicationEventRepository.save(applicationEvent);

        event.getApplicationEventList().add(applicationEvent);
        eventEntityRepository.save(event);

         userEntityRepository.save(user);


        return applicationEventFactory.makeApplicationEventDto(applicationEvent);

    }

    public void cancelApplication(Long eventId, UpdateApplicationDtoRequest applicationEventDto) throws AccessDeniedException {

        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method cancelApplication: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });


        logger.info("Canceling application for Event ID: {}, User ID: {}", eventId,userId);

       EventEntity event = controllerHelper.getEventOrThrowException(eventId);

        ApplicationEvent  applicationEvent =  controllerHelper.getApplicationEventOrThrowException(applicationEventDto.getApplicationId());


        UserEntity applicant = applicationEvent.getUser();

        if (!applicant.getUserId().equals(userId)) {
            logger.warn("User does not have permission to cancel this application");
            throw new AccessDeniedException("User does not have permission to cancel this application");
        }

        applicant.getApplicationEventList().remove(applicationEvent);
        userEntityRepository.save(applicant);


        applicationEventRepository.deleteById(applicationEventDto.getApplicationId());
        logger.info("Application with ID {} successfully deleted", applicationEventDto.getApplicationId());

    }

    public ApplicationEventResponseDto approveApplication(Long eventId, UpdateApplicationDtoRequest applicationEventDto) throws RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = controllerHelper.getUserOrThrowException(userId);

        ApplicationEvent applicationEvent = controllerHelper.getApplicationEventOrThrowException(applicationEventDto.getApplicationId());

        logger.info("Approve application method for Event ID: {}, User ID: {}", eventId, userId);

        EventEntity event = controllerHelper.getEventOrThrowException(eventId);

        OrganizationEntity organizationEntity = event.getOrganization();

        if (organizationEntity == null) {
            logger.warn("Event {} is not associated with any organizations", eventId);
            throw new NotFoundException("Event is not associated with any organization");
        }

        if (!userRoleChecker.isOrganizer(userId, organizationEntity)) {
            logger.warn("User {} isn't an organizer for the organization in event {} in method rejectApplication", userId, eventId);
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }

        try {
            if (applicationEvent.getEvent() != null) {
                applicationEvent.setStatus(Status.APPROVED);
                applicationEvent.setEvent(event);
                event.getApplicationEventList().add(applicationEvent);
                logger.info("Event added to applicationEvent and applicationEvent added to event.");
            }
        } catch (Exception e) {
            logger.error("Error occurred while adding event to applicationEvent or vice versa: {}", applicationEvent, e);
        }

        UserEntity applicant = applicationEvent.getUser();
            try {
                event.getUserList().add(applicant);
                applicant.getEventList().add(event);

                UserEventRole userEventRole = new UserEventRole();
                userEventRole.setUser(applicant);
                userEventRole.setEvent(event);
                userEventRole.setRole(applicationEvent.getRole());

                applicant.getUserEventRoles().add(userEventRole);

                userEventRoleRepository.save(userEventRole);


                logger.info("Successfully processed user add: {}", applicant);
            } catch (Exception e) {
                logger.error("Failed to process user: {}", applicant, e);
            }


        eventEntityRepository.save(event);
        applicationEventRepository.save(applicationEvent);

        return applicationEventFactory.makeApplicationEventDto(applicationEvent);
    }

    // Method for the organizer to reject the application
    public void rejectApplication(Long eventId, UpdateApplicationDtoRequest applicationEventDto) throws RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method rejectApplication: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });


        logger.info("Rejecting application for Event ID: {}, User ID: {}", eventId, userId);


        ApplicationEvent  applicationEvent =  controllerHelper.getApplicationEventOrThrowException(applicationEventDto.getApplicationId());

        EventEntity event = controllerHelper.getEventOrThrowException(eventId);

        OrganizationEntity organizationEntity = event.getOrganization();

        if (organizationEntity == null) {
            logger.warn("Event {} is not associated with any organizations in method rejectApplication", eventId);
            throw new NotFoundException("Event is not associated with any organization");
        }

        if (!userRoleChecker.isOrganizer(userId, organizationEntity)) {
            logger.warn("User {} isn't an organizer for the organization in event {} in method rejectApplication", userId, eventId);
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }

        if (!applicationEvent.getStatus().equals(Status.REJECTED)) {
            applicationEvent.setStatus(Status.REJECTED);
            applicationEventRepository.save(applicationEvent);
            logger.info("Application with ID {} status changed to REJECTED.", applicationEvent.getApplicationId());
        } else {
            logger.warn("Attempted to change status of application ID {} to REJECTED, but it is already in that status.", applicationEvent.getApplicationId());
        };

    }

    public List<ApplicationEventResponseDto> getApplicationsForEvent(Long eventId) throws RoleNotAllowedException {

        Long userId = authenticationService.getCurrentUserId();

        UserEntity user = userEntityRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Method getApplicationsForEvent: User with ID {} not found",userId );
                    return new NotFoundException("User not found");
                });

        logger.info("getApplications application for Event ID: {}, User ID: {}", eventId, userId);

        EventEntity event = controllerHelper.getEventOrThrowException(eventId);
        Long organizationId = event.getOrganization().getOrganizationId();

        OrganizationEntity organizationEntity = event.getOrganization();

        if (!userRoleChecker.isOrganizer(userId, organizationEntity)) {
            logger.warn("User {} isn't an organizer for the organization in event {} in method rejectApplication", userId, eventId);
            throw new RoleNotAllowedException("User is not authorized to approve this application");
        }

        if (!userRoleChecker.isOrganizer(userId, organizationEntity)) {
            logger.warn("Method getApplicationsForEvent: {} isn't an organizer for any organization in event {}", userId, eventId);
            throw new RoleNotAllowedException("User isn't authorized to approve this application");
        }

        List<ApplicationEvent> pendingApplications = event.getApplicationEventList().stream()
                .filter(application -> application.getStatus() == Status.PENDING)
                .toList();

        return pendingApplications.stream()
                .map(applicationEventFactory::makeApplicationEventDto)
                .collect(Collectors.toList());
    }

    public boolean isApplicationForEventAlreadySubmitted(Long eventId, Long userId) {
        return applicationEventRepository.existsByEvent_eventIdAndUser_userId(eventId, userId);
    }
}
