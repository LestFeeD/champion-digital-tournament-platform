package zChampions.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.enumsEntities.Status;
import zChampions.catalogue.exceptions.*;
import zChampions.catalogue.factories.ApplicationEventFactory;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.UpdateApplicationDtoRequest;
import zChampions.catalogue.requestDto.UpdateApplicationEventCreateDto;
import zChampions.catalogue.responseDto.ApplicationEventResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationEventServiceTests {

    @Mock
    private ControllerHelper controllerHelper;
    @Mock
    private ApplicationRepository applicationEventRepository;
    @Mock
    private ApplicationEventFactory applicationEventFactory;

    @Mock
    private UserRepository userEntityRepository;
    @Mock
    private EventRepository eventEntityRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserEventRoleRepository userEventRoleRepository;

    @Mock
    private UserOrganizationRoleRepository userOrganizationRoleRepository;

    @Mock
    private UserRoleChecker userRoleChecker;

    @InjectMocks
    private ApplicationEventService applicationEventService;



    @Test
    void submitApplication_withValidRequest_returnApplicationEventResponseDto() throws BadRequestException {

        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setApplicationEventList(new HashSet<>());


        Long eventId = 1L;
        EventEntity event = new EventEntity();
        event.setEventId(eventId);

        Set<UserEntity> users = new HashSet<>();
        event.setUserList(users);
        event.setApplicationEventList(new HashSet<>());

        UpdateApplicationEventCreateDto applicationEventCreateDto = new UpdateApplicationEventCreateDto();
        applicationEventCreateDto.setRole(RoleSport.SPORTSMAN);

        ApplicationEventResponseDto expectedResponseDto = ApplicationEventResponseDto.builder().build();

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));

        ApplicationEvent applicationEvent = ApplicationEvent.builder()
                .user(user)
                .status(Status.PENDING)
                .build();

        when(applicationEventRepository.save(any(ApplicationEvent.class))).thenReturn(applicationEvent);
        when(userEntityRepository.save(user)).thenReturn(user);
        when(eventEntityRepository.save(event)).thenReturn(event);
        when(applicationEventFactory.makeApplicationEventDto(any(ApplicationEvent.class))).thenReturn(expectedResponseDto);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);

        ApplicationEventResponseDto actualResponseDto = applicationEventService.submitApplication(
                eventId, applicationEventCreateDto);

        assertEquals(expectedResponseDto, actualResponseDto);
        assertEquals(RoleSport.SPORTSMAN, applicationEventCreateDto.getRole());

        verify(applicationEventRepository, times(1)).save(any(ApplicationEvent.class));
        verify(userEntityRepository, times(1)).save(user);
        verify(eventEntityRepository, times(1)).save(event);
        verify(applicationEventFactory, times(1)).makeApplicationEventDto(any(ApplicationEvent.class));

    }
    @Test
    void submitApplication_ApplicationAlreadySubmitted_returnDuplicateApplicationException() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        Long eventId = 1L;
        EventEntity event = new EventEntity();
        event.setEventId(eventId);

        ApplicationEvent applicationEvent = new ApplicationEvent();

        UpdateApplicationEventCreateDto applicationEventCreateDto = new UpdateApplicationEventCreateDto();

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventEntityRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(applicationEventRepository.existsByEvent_eventIdAndUser_userId(eventId, userId))
                .thenReturn(true);

        DuplicateApplicationException exception = assertThrows(DuplicateApplicationException.class, () -> {
            applicationEventService.submitApplication(eventId, applicationEventCreateDto);
        });

        assertEquals("Заявка для этого мероприятия подана.", exception.getMessage());

        verify(applicationEventRepository, times(0)).save(applicationEvent);
        verify(eventEntityRepository, times(0)).save(event);
        verify(userEntityRepository, times(0)).save(user);
    }

    @Test
    void cancelApplication_deleteApplicationRequest_DeleteByIdApplicationInEvent() throws AccessDeniedException {

        Long applicationId = 1L;
        Long eventId = 1L;
        Long userId = 1L;

        EventEntity event = new EventEntity();
        event.setEventId(1L);

        UserEntity user = new UserEntity();
        user.setUserId(userId);



        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setApplicationId(applicationId);
        applicationEvent.setUser(user);


        Set<ApplicationEvent> applicationEvents = new HashSet<>();
        applicationEvents.add(applicationEvent);
        user.setApplicationEventList(applicationEvents);

        UpdateApplicationDtoRequest applicationEventDto = new UpdateApplicationDtoRequest();
        applicationEventDto.setApplicationId(applicationId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);


        applicationEventService.cancelApplication(eventId, applicationEventDto);

        verify(applicationEventRepository, times(1)).deleteById(applicationId);

        verify(userEntityRepository, times(1)).save(user);

    }

    @Test
    void cancelApplication_deleteApplicationRequest_returnAccessDeniedException() throws AccessDeniedException {
        Long applicationId = 1L;
        Long eventId = 1L;
        Long userId = 1L;


        EventEntity event = new EventEntity();
        event.setEventId(1L);

        UserEntity anotherUser = new UserEntity();
        anotherUser.setUserId(2L);



        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setApplicationId(applicationId);
        applicationEvent.setUser(anotherUser);

        UpdateApplicationDtoRequest applicationEventDto = new UpdateApplicationDtoRequest();
        applicationEventDto.setApplicationId(applicationId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);


        assertThrows(AccessDeniedException.class, () ->
                applicationEventService.cancelApplication(eventId, applicationEventDto));


        verify(applicationEventRepository, times(0)).save(applicationEvent);
        verify(eventEntityRepository, times(0)).save(event);
        verify(userEntityRepository, times(0)).save(anotherUser);
    }


    @Test
    void rejectApplication_rejectApplicationInEventByOrganizer_SetRejectApplication() throws RoleNotAllowedException {

        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setApplicationId(applicationId);
        applicationEvent.setStatus(Status.PENDING);

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEvent);

        EventEntity event = new EventEntity();
        event.setEventId(eventId);
        event.setApplicationEventList(applicationEventList);
        applicationEvent.setEvent(event);


        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        applicationEventService.rejectApplication(eventId, applicationDtoRequest);

        assertEquals(Status.REJECTED, applicationEvent.getStatus());


        verify(applicationEventRepository, times(1)).save(applicationEvent);

    }

    @Test
    void rejectApplication_rejectApplicationInEventByOrganizer_returnRoleNotAllowedException() throws RoleNotAllowedException {

        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setApplicationId(applicationId);
        applicationEvent.setStatus(Status.PENDING);

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEvent);

        EventEntity event = new EventEntity();
        event.setEventId(eventId);
        event.setApplicationEventList(applicationEventList);
        applicationEvent.setEvent(event);


        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> applicationEventService.rejectApplication(eventId, applicationDtoRequest));

        verify(applicationEventRepository, never()).save(applicationEvent);

    }

    @Test
    void approveApplication_approveApplicationInEventByOrganizer_returnDtoApplication() throws RoleNotAllowedException {
        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEvent = new ApplicationEvent();
        applicationEvent.setStatus(Status.PENDING);

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEvent);

        EventEntity event = new EventEntity();
        event.setApplicationEventList(applicationEventList);


        UserEntity organizationUser = new UserEntity();
        organizationUser.setUserId(userId);


        Set<EventEntity> events = new HashSet<>();
        events.add(event);
        applicationEvent.setEvent(event);

        UserEntity user = new UserEntity();
        event.setUserList(new HashSet<>(Collections.singletonList(user)));
        user.setEventList(events);
        user.setApplicationEventList(applicationEventList);
        applicationEvent.setUser(user);

        UserEventRole userEventRole = new UserEventRole();
        userEventRole.setUser(user);
        user.setUserEventRoles(Collections.singletonList(userEventRole));

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);


        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(organizationUser);
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEventRoleRepository.save(any(UserEventRole.class))).thenReturn(userEventRole);

        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        applicationEventService.approveApplication(eventId, applicationDtoRequest);

        assertEquals(Status.APPROVED, applicationEvent.getStatus());
        verify(applicationEventRepository, times(1)).save(applicationEvent);
        verify(userEntityRepository, times(1)).save(user);
        verify(eventEntityRepository, times(1)).save(event);
        verify(userEventRoleRepository, times(1)).save(any(UserEventRole.class));
    }

    @Test
    void approveApplication_unrelatedEventWithOrganization_returnNotFoundException() throws RoleNotAllowedException {
        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEvent = new ApplicationEvent();

        EventEntity event = new EventEntity();
        event.setOrganization(new OrganizationEntity());


        applicationEvent.setEvent(event);

        UserEntity user = new UserEntity();

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setEvent(new ArrayList<>());
        organization.setUsers(new HashSet<>());




        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);

      assertThrows(NotFoundException.class, () -> applicationEventService.approveApplication(eventId, applicationDtoRequest));

        verify(applicationEventRepository, never()).save(applicationEvent);
        verify(userEntityRepository, never()).save(user);
        verify(eventEntityRepository, never()).save(event);
    }

    @Test
    void approveApplication_insufficientUserRights_returnRoleNotAllowedException(){
        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEvent = new ApplicationEvent();

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEvent);

        EventEntity event = new EventEntity();
        event.setApplicationEventList(applicationEventList);
        applicationEvent.setEvent(event);

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        event.setUserList(new HashSet<>(Collections.singletonList(user)));
        user.setEventList(new HashSet<>(Collections.singletonList(event)) );
        user.setApplicationEventList(applicationEventList);
        applicationEvent.setUser(user);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);


        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(controllerHelper.getApplicationEventOrThrowException(applicationId)).thenReturn(applicationEvent);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

         assertThrows(RoleNotAllowedException.class, () -> applicationEventService.approveApplication(eventId, applicationDtoRequest));


        verify(applicationEventRepository, never()).save(applicationEvent);
        verify(userEntityRepository,never()).save(user);
        verify(eventEntityRepository, never()).save(event);
    }

    @Test
    void getApplicationsForEvent_gettingListApplicationsForOrganizer_returnDtoApplications() throws RoleNotAllowedException {
        Long applicationId = 1L;
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEventOne = new ApplicationEvent();
        applicationEventOne.setStatus(Status.PENDING);

        ApplicationEvent applicationEventTwo = new ApplicationEvent();
        applicationEventTwo.setStatus(Status.PENDING);

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEventOne);
        applicationEventList.add(applicationEventTwo);

        EventEntity event = new EventEntity();
        event.setApplicationEventList(applicationEventList);

        applicationEventOne.setEvent(event);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);


        UpdateApplicationDtoRequest applicationDtoRequest = new UpdateApplicationDtoRequest();
        applicationDtoRequest.setApplicationId(applicationId);

        ApplicationEventResponseDto dtoOne = Mockito.mock(ApplicationEventResponseDto.class);
        ApplicationEventResponseDto dtoTwo = Mockito.mock(ApplicationEventResponseDto.class);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);
        when(applicationEventFactory.makeApplicationEventDto(applicationEventOne)).thenReturn(dtoOne);
        when(applicationEventFactory.makeApplicationEventDto(applicationEventTwo)).thenReturn(dtoTwo);

        List<ApplicationEventResponseDto> result = applicationEventService.getApplicationsForEvent(eventId);

        assertEquals(2, result.size());

        verify(applicationEventFactory, times(1)).makeApplicationEventDto(applicationEventOne);
        verify(applicationEventFactory, times(1)).makeApplicationEventDto(applicationEventTwo);

    }

    @Test
    void getApplicationsForEvent_UserDidNotPassTheVerificationForTheOrganizer_RoleNotAllowedException() throws RoleNotAllowedException {
        Long userId = 1L;
        Long eventId = 1L;

        ApplicationEvent applicationEventOne = new ApplicationEvent();
        applicationEventOne.setStatus(Status.PENDING);

        ApplicationEvent applicationEventTwo = new ApplicationEvent();
        applicationEventTwo.setStatus(Status.PENDING);

        Set<ApplicationEvent> applicationEventList = new HashSet<>();
        applicationEventList.add(applicationEventOne);
        applicationEventList.add(applicationEventTwo);

        EventEntity event = new EventEntity();
        event.setApplicationEventList(applicationEventList);

        applicationEventOne.setEvent(event);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(1L);
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));
        organization.setEvent(Collections.singletonList(event));
        event.setOrganization(organization);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(event);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);


        assertThrows(RoleNotAllowedException.class, () -> applicationEventService.getApplicationsForEvent(eventId));

        verify(applicationEventFactory, never()).makeApplicationEventDto(applicationEventOne);
        verify(applicationEventFactory, never()).makeApplicationEventDto(applicationEventTwo);

    }
    }

