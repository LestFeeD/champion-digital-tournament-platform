package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.factories.EventFactory;
import zChampions.catalogue.requestDto.createRequest.CreateEventDtoRequest;
import zChampions.catalogue.requestDto.updateRequest.UpdateEventRequestDto;
import zChampions.catalogue.responseDto.EventResponseDto;
import zChampions.catalogue.responseDto.UserEventResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class EventEntityService {

    private final EventRepository eventEntityRepository;
    private final EventFactory eventDtoFactory;
    private final ControllerHelper controllerHelper;
    private final OrganizationRepository organizationEntityRepository;
    private final ValidationErrors validationErrors;
    private final AuthenticationService authenticationService;
    private final ApplicationRepository applicationEventRepository;
    private final UserRoleChecker userRoleChecker;
    private final UserRepository userRepository;
    private final UserEventRoleRepository userEventRoleRepository;

    private  final static Logger logger = LoggerFactory.getLogger(EventEntityService.class);


    public List<EventResponseDto> findAllEvent() throws NotFoundException {

        List<EventEntity> events = eventEntityRepository.findAll();
        if (events == null || events.isEmpty()) {
            logger.warn("No events were found for the findAllEvent query");
            return new ArrayList<>();
        } else {
            logger.info("Found {} events", events.size());
            return events.stream()
                    .map(eventDtoFactory::makeEventDto)
                    .collect(Collectors.toList());
        }
    }

    public List<UserEventResponseDto> findUsersInEvent(Long eventId) throws NotFoundException {
        List<Object[]> users = eventEntityRepository.findUsersWithEvent(eventId);
        if (users == null || users.isEmpty()) {
            logger.warn("No users were found for by event {}", eventId);
            return new ArrayList<>();
        } else {
            return users.stream()
                    .map(record -> new UserEventResponseDto(
                            (String) record[1],  // first_name
                            (String) record[2],  // last_name
                            (String) record[3]   // email
                    ))
                    .collect(Collectors.toList());
        }

    }

        public List<EventResponseDto> findByParameters(String city, KindOfSport kindOfSport) throws NotFoundException {
       logger.info("Find by parameters by city: {} and kind of sport: {}", city, kindOfSport);
        List<EventEntity> events;

        if (city != null && kindOfSport != null) {
            events =  eventEntityRepository.findByCityAndKindOfSport(city, kindOfSport);
        } else if (city != null) {
            events =  eventEntityRepository.findByCity(city);
        } else if (kindOfSport != null) {
            events =  eventEntityRepository.findByKindOfSport(kindOfSport);
        } else {
            logger.warn("According to the parameter of the city: {} and the kind of sport: {}, it was not possible to find a single event", city, kindOfSport);
            events = new ArrayList<>();
        }

        return events.stream()
                .map(eventDtoFactory::makeEventDto)
                .collect(Collectors.toList());
    }

    public List<EventResponseDto> findEventByUser(Long userId) throws NotFoundException {
        logger.info("Getting the user ID: {}", userId);
        List<EventEntity> listEvent = eventEntityRepository.findAllByUserList_UserId(userId);

        if (listEvent.isEmpty()) {
            logger.warn("The user doesn't have any organizations according to the ID: {}", userId);
            listEvent = new ArrayList<>();
        }

        return listEvent.stream()
                .map(eventDtoFactory::makeEventDto)
                .collect(Collectors.toList());
    }

    public EventResponseDto createEvent(
            @Valid CreateEventDtoRequest eventCreateRequest, BindingResult bindingResult) throws  BadRequestException {

        Long organizerId = authenticationService.getCurrentUserId();

        UserEntity user = userRepository.findById(organizerId)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found",organizerId);
                    return new NotFoundException("User not found");
                });


        if(bindingResult.hasErrors() ) {
           String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while creating event: {}", result);
            throw new BadRequestException(result);

        }

        OrganizationEntity organization = organizationEntityRepository.findById(eventCreateRequest.getOrganizationIds().get(0))
                .orElseThrow(() -> {
                    logger.warn("Organization with ID {} not found",eventCreateRequest.getOrganizationIds().get(0));
                    return new NotFoundException("Organization not found");
                });

        List<Long> organizationIds = eventCreateRequest.getOrganizationIds();
        Long organizationId = organizationIds.get(0);
        int count = organizationEntityRepository.existsByIdAndUserId(organizationId, organizerId);
        boolean hasAccess = count > 0;
        if (!hasAccess) {
            logger.warn("User with ID {} does not have access to organization with ID {}", organizerId, organizationId);
            throw new BadRequestException("User does not have permission to access this organization");
        }

        if(eventCreateRequest.getEndsAt().isBefore(eventCreateRequest.getCreatedAt())) {
            throw new BadRequestException("The end date cannot be earlier than the start date");
        }


        EventEntity event = EventEntity.builder()
                .title(eventCreateRequest.getTitle())
                .kindOfSport(eventCreateRequest.getKindOfSport())
                .region(eventCreateRequest.getRegion())
                .city(eventCreateRequest.getCity())
                .createdAt(eventCreateRequest.getCreatedAt())
                .endsAt(eventCreateRequest.getEndsAt())
                .information(eventCreateRequest.getInformation())
                .comments(eventCreateRequest.getComments())
                .organization(organization)
                .build();
        final EventEntity savedEvent = eventEntityRepository.saveAndFlush(event);
        logger.info("Event with ID {} successfully created", savedEvent.getEventId());



        organization.getEvent().add(savedEvent);
        organizationEntityRepository.save(organization);
        logger.info("Added event with ID {} to organization with ID {}", savedEvent.getEventId(), organization.getOrganizationId());

        return eventDtoFactory.makeEventDto(savedEvent);
    }

    public EventResponseDto findEvent(Long eventId)  {
            EventEntity entity = controllerHelper.getEventOrThrowException(eventId);
            return eventDtoFactory.makeEventDto(entity);
    }

    public ResponseEntity<?> updateEvent(Long eventId,
                                         @Valid UpdateEventRequestDto eventEntityDto, BindingResult bindingResult) throws BadRequestException, AccessDeniedException {
        Long organizerId = authenticationService.getCurrentUserId();

        UserEntity user = userRepository.findById(organizerId)
                .orElseThrow(() -> {
                    logger.warn("User with ID {} not found",organizerId);
                    return new NotFoundException("User not found");
                });

        logger.info("Find by parameters for update event, title: {}, createdAt: {}, endsAt: {}, information: {}, comments: {}, userId {}", eventEntityDto.getTitle(),
                eventEntityDto.getCreatedAt(), eventEntityDto.getEndsAt(), eventEntityDto.getInformation(), eventEntityDto.getComments(), organizerId);

        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while updating event: {}", result);
            throw new BadRequestException(result);

        }     // Проверка дат на null перед сравнением
        if (eventEntityDto.getCreatedAt() != null && eventEntityDto.getEndsAt() != null) {
            if (eventEntityDto.getCreatedAt().isAfter(eventEntityDto.getEndsAt())) {
                logger.error("The final result: {},  cannot be earlier than the created one: {}", eventEntityDto.getEndsAt(), eventEntityDto.getCreatedAt());
                throw new BadRequestException("Дата начала не может быть позже даты окончания.");
            }
        }

        List<Long> organizationIds = eventEntityDto.getOrganizationIds();
        Long organizationId = organizationIds.get(0);
        int count = organizationEntityRepository.existsByIdAndUserId(organizationId, organizerId);
        boolean hasAccess = count > 0;
        if (!hasAccess) {
            logger.warn("User with ID {} does not have access to organization with ID {} in update method", organizerId, organizationId);
            throw new BadRequestException("User does not have permission to access this organization");
        }

        EventEntity updateEvent = controllerHelper.getEventOrThrowException(eventId);

        if (eventEntityDto.getTitle() != null) {
            updateEvent.setTitle(eventEntityDto.getTitle());
        }
        if (eventEntityDto.getCreatedAt() != null) {
            updateEvent.setCreatedAt(eventEntityDto.getCreatedAt());
        }
        if (eventEntityDto.getEndsAt() != null) {
            updateEvent.setEndsAt(eventEntityDto.getEndsAt());
        }
        if (eventEntityDto.getInformation() != null) {
            updateEvent.setInformation(eventEntityDto.getInformation());
        }
        if (eventEntityDto.getComments() != null) {
            updateEvent.setComments(eventEntityDto.getComments());
        }

        EventEntity savedEvent = eventEntityRepository.saveAndFlush(updateEvent);
        logger.info("Event with ID {} successfully updated.", savedEvent.getEventId());

        return ResponseEntity.ok(savedEvent);
    }

    public void deleteUserInEvent(Long eventId, Long userId) throws RoleNotAllowedException {
        Long organizerId = authenticationService.getCurrentUserId();

        EventEntity event = controllerHelper.getEventOrThrowException(eventId);

        OrganizationEntity organizationEntity = event.getOrganization();

        UserEntity user = userRepository.findById(userId).orElseThrow();

        Long organizationId = organizationEntity.getOrganizationId();
        int count = organizationEntityRepository.existsByIdAndUserId(organizationId, organizerId);

        if (count == 0) {
            logger.error("User {} does not have organizer rights for organization {} in event {}", organizerId, organizationId, eventId);
            throw new RoleNotAllowedException("Organizer is not authorized to remove users from this event");
        }

        Long applicationId = applicationEventRepository.findApplicationId(eventId, userId);
        logger.info("applicationId: {}", applicationId);
        ApplicationEvent applicationEvent = applicationEventRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found for user " + userId + " in organization " + eventId));

        Long userEventRoleId = userEventRoleRepository.findUserEventRoleId(eventId, userId );
        logger.info("userEventRoleId: {}", userEventRoleId);

        UserEventRole userEventRole = userEventRoleRepository.findById(userEventRoleId)
                .orElseThrow(() -> new NotFoundException("Application not found for user " + userId + " in organization " + eventId));



        event.getApplicationEventList().remove(applicationEvent);

        event.getUserEventRoles().remove(userEventRole);
        user.getUserEventRoles().remove(userEventRole);
        userEventRoleRepository.delete(userEventRole);
logger.info("userRoleEvent {}", userEventRole.getEventRoleId());
        applicationEventRepository.delete(applicationEvent);
        event.getUserList().removeIf(app -> app.getUserId().equals(userId));

        eventEntityRepository.save(event);

    }


    public void deleteProduct(Long eventId) {
        controllerHelper.getEventOrThrowException(eventId);
        eventEntityRepository.deleteById(eventId);
        ResponseEntity.noContent()
                .build();
    }

}
