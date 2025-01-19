package zChampions.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.EventEntity;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.factories.EventFactory;
import zChampions.catalogue.repository.EventRepository;
import zChampions.catalogue.repository.OrganizationRepository;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.requestDto.createRequest.CreateEventDtoRequest;
import zChampions.catalogue.requestDto.updateRequest.UpdateEventRequestDto;
import zChampions.catalogue.responseDto.EventResponseDto;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static zChampions.catalogue.enumsEntities.KindOfSport.FOOTBALL;
import static zChampions.catalogue.enumsEntities.KindOfSport.SWIMMING;

@ExtendWith(MockitoExtension.class)
 class  EventEntityServiceTests {

    @Mock
    private EventRepository eventEntityRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock private ValidationErrors validationErrors;

    @Mock
    private EventFactory eventDtoFactory;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ControllerHelper controllerHelper;

    @Mock
    private  UserRoleChecker userRoleChecker;

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationService authenticationService;



    @InjectMocks
    private EventEntityService eventEntityService;

    @Test
    void createEvent_withValidParameters_returnsEventEntityDto() throws BadRequestException {
        Long userId =1L;
        Long eventId = 1L;
        String title = "Test Event";
        KindOfSport kindOfSport = FOOTBALL;
        String region = "Region1";
        String city = "City1";
        LocalDate createdAt = LocalDate.of(2023, 7, 4);
        LocalDate endsAt = LocalDate.of(2023, 7, 5);
        String information = "Test information";
        String comments = "Test comments";
        Long organizationId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setEvent(new ArrayList<>());
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));



        EventEntity eventEntity = EventEntity.builder()
                .eventId(eventId)
                .title(title)
                .kindOfSport(kindOfSport)
                .region(region)
                .city(city)
                .createdAt(createdAt)
                .endsAt(endsAt)
                .information(information)
                .comments(comments)
                .organization(organization)
                .build();


        CreateEventDtoRequest eventCreateRequest = CreateEventDtoRequest.builder()
                .title(title)
                .kindOfSport(kindOfSport)
                .region(region)
                .city(city)
                .createdAt(createdAt)
                .endsAt(endsAt)
                .information(information)
                .comments(comments)
                .organizationIds(Collections.singletonList(organizationId))
                .build();

        EventResponseDto eventResponseDto = EventResponseDto.builder()
                .eventId(eventId)
                .title(title)
                .kindOfSport(kindOfSport)
                .region(region)
                .city(city)
                .createdAt(createdAt)
                .endsAt(endsAt)
                .information(information)
                .comments(comments)
                .organizationIds(organizationId)
                .build();



        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(eventEntityRepository.saveAndFlush(any(EventEntity.class))).thenReturn(eventEntity);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventDtoFactory.makeEventDto(any(EventEntity.class))).thenReturn(eventResponseDto);
        when(organizationRepository.save(organization)).thenReturn(organization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(organizationRepository.existsByIdAndUserId(organizationId, userId)).thenReturn(1);
        when(bindingResult.hasErrors()).thenReturn(false);

        EventResponseDto result = eventEntityService.createEvent(
                eventCreateRequest, bindingResult);

        assertNotNull(result);
        assertEquals(eventResponseDto, result);

        verify(eventEntityRepository, times(1)).saveAndFlush(any(EventEntity.class));
        verify(eventDtoFactory, times(1)).makeEventDto(eventEntity);
        verify(organizationRepository, times(1)).save(any(OrganizationEntity.class));
    }

    @Test
    void createEvent_endDateLessThanCreatedAt_ReturnBadRequest() throws BadRequestException {
        Long userId = 1L;

        CreateEventDtoRequest eventDtoRequest = new CreateEventDtoRequest();
        eventDtoRequest.setTitle("Test Event");
        eventDtoRequest.setKindOfSport(FOOTBALL);
        eventDtoRequest.setRegion("Region");
        eventDtoRequest.setCity("City");
        eventDtoRequest.setCreatedAt(LocalDate.now().plusDays(1));
        eventDtoRequest.setEndsAt(LocalDate.now());
        eventDtoRequest.setInformation("Information");
        eventDtoRequest.setComments("Comments");
        eventDtoRequest.setOrganizationIds(Collections.singletonList(1L));

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bindingResult.hasErrors()).thenReturn(false);
        OrganizationEntity mockOrganization = Mockito.mock(OrganizationEntity.class);

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(mockOrganization));

        assertThrows(BadRequestException.class, () -> {
            eventEntityService.createEvent( eventDtoRequest, bindingResult);
        });

        verify(eventEntityRepository, never()).saveAndFlush(any(EventEntity.class));

    }

    @Test
    void createEvent_NotFoundOrganization_ReturnNotFoundException() {
        Long id = 0L;
        Long userId = 1L;

        CreateEventDtoRequest eventDtoRequest = new CreateEventDtoRequest();
        eventDtoRequest.setOrganizationIds(Collections.singletonList(id));
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(organizationRepository.findById(id)).thenReturn(Optional.empty());


       assertThrows(NotFoundException.class, () -> {
            eventEntityService.createEvent( eventDtoRequest, bindingResult);
        });

        verify(eventEntityRepository, never()).saveAndFlush(any(EventEntity.class));
    }


    @Test
    void createEvent_withoutParameterTitle_ReturnBadRequest() throws BadRequestException {
        Long userId = 1L;

        String title = null;
        KindOfSport kindOfSport = FOOTBALL;
        String region = "Region1";
        String city = "City1";
        LocalDate createdAt = LocalDate.of(2023, 7, 4);
        LocalDate endsAt = LocalDate.of(2023, 7, 5);
        String information = "Test information";
        String comments = "Test comments";
        Long organizationId = 1L;

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bindingResult.hasErrors()).thenReturn(true);
        when(validationErrors.getValidationErrors(any(BindingResult.class))).thenReturn(Collections.singletonList("Ошибки валидации: title is null").toString());



        CreateEventDtoRequest eventCreateRequest = CreateEventDtoRequest.builder()
                .title(title)
                .kindOfSport(kindOfSport)
                .region(region)
                .city(city)
                .createdAt(createdAt)
                .endsAt(endsAt)
                .information(information)
                .comments(comments)
                .organizationIds(Collections.singletonList(organizationId))
                .build();

        assertThrows(BadRequestException.class, () -> {
            eventEntityService.createEvent(eventCreateRequest, bindingResult);});

        verify(eventEntityRepository, never()).saveAndFlush(any(EventEntity.class));
        verify(eventDtoFactory, never()).makeEventDto(any(EventEntity.class));
    }


    @Test
    void findByParameters_CityAndKindOfSport_ReturnEventWithTheseParameters() {

        KindOfSport kindOfSport = SWIMMING;
        String city = "City1";

        eventEntityService.findByParameters(city, kindOfSport);

        verify(eventEntityRepository).findByCityAndKindOfSport(city, kindOfSport);

    }

    @Test
    void findByParameters_CityAndKindOfSport_ReturnEmptyCollections() {

        String city = null;
        KindOfSport kindOfSport = null;

        List<EventResponseDto> result = eventEntityService.findByParameters(city, kindOfSport);

        assertTrue(result.isEmpty());

    }

    @Test
    void findByParameters_KindOfSportOnly_ReturnKindOfSport() {

        KindOfSport kindOfSport = SWIMMING;

        eventEntityService.findByParameters(null, kindOfSport);

        verify(eventEntityRepository).findByKindOfSport( kindOfSport);

    }

    @Test
    void findByParameters_CityOnly_ReturnCityOnly() {

        String city = "City";
        eventEntityService.findByParameters(city, null);

        verify(eventEntityRepository).findByCity(city);

    }

    @Test
    void findAllEvent_ReturnsListOfEventDto()  {

        EventEntity eventOne = new EventEntity();
        EventEntity eventTwo = new EventEntity();
        List<EventEntity> events = Arrays.asList(eventOne, eventTwo);

        when(eventEntityRepository.findAll()).thenReturn(events);

        EventResponseDto dtoOne = mock(EventResponseDto.class);
        EventResponseDto dtoTwo = mock(EventResponseDto.class);
        when(eventDtoFactory.makeEventDto(eventOne)).thenReturn(dtoOne);
        when(eventDtoFactory.makeEventDto(eventTwo)).thenReturn(dtoTwo);

        List<EventResponseDto> result = eventEntityService.findAllEvent();

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result list size should match");
        verify(eventEntityRepository).findAll();
    }

    @Test
    void findAllEvent_ReturnEmptyCollections() {
        when(eventEntityRepository.findAll()).thenReturn(null);


        List<EventResponseDto> result = eventEntityService.findAllEvent();

        assertTrue(result.isEmpty());


    }

    @Test
    void findEvent_FindEventById_ReturnEvent() {
        Long id = 1L;
       final EventEntity eventEntity = mock(EventEntity.class);

        final EventResponseDto eventEntityDto = mock(EventResponseDto.class);

        when(controllerHelper.getEventOrThrowException(id)).thenReturn(eventEntity);
        when(eventDtoFactory.makeEventDto(eventEntity)).thenReturn(eventEntityDto);

        EventResponseDto result = eventEntityService.findEvent(id);


        verify(controllerHelper, times(1)).getEventOrThrowException(id);
        verify(eventDtoFactory, times(1)).makeEventDto(eventEntity);

    }
    @Test
    void updateEvent_UpdateEventById_ReturnUpdateEvent() throws BadRequestException, AccessDeniedException {
        Long eventId = 1L;
        Long userId = 1L;
        Long organizationId = 1L;

        UpdateEventRequestDto eventDto = new UpdateEventRequestDto();
        eventDto.setTitle("Updated Event");
        eventDto.setCreatedAt(LocalDate.of(2024, 10, 10));
        eventDto.setEndsAt(LocalDate.of(2024, 12, 11));
        eventDto.setInformation("Updated information");
        eventDto.setComments("Updated comments");
        eventDto.setOrganizationIds(Collections.singletonList(organizationId));


        when(bindingResult.hasErrors()).thenReturn(false);

        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventId(eventId);
        eventEntity.setTitle("Original Title");

        UserEntity user = new UserEntity();
        user.setUserId(userId);


        when(controllerHelper.getEventOrThrowException(eventId)).thenReturn(eventEntity);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventEntityRepository.saveAndFlush(any(EventEntity.class))).thenReturn(eventEntity);
        when(organizationRepository.existsByIdAndUserId(userId, organizationId)).thenReturn(1);


        ResponseEntity<?> response = eventEntityService.updateEvent(eventId, eventDto, bindingResult);

        assertEquals("Updated Event", eventEntity.getTitle());
        assertEquals(LocalDate.of(2024, 10, 10), eventEntity.getCreatedAt());
        assertEquals(LocalDate.of(2024, 12, 11), eventEntity.getEndsAt());
        assertEquals("Updated information", eventEntity.getInformation());
        assertEquals("Updated comments", eventEntity.getComments());

        verify(eventEntityRepository, times(1)).saveAndFlush(eventEntity);
    }

    @Test
    void updateEvent_endDateLessThanCreatedAt_ReturnBadRequest() {
        Long id = 1L;
        Long userId = 1L;
        UpdateEventRequestDto eventDtoRequest = new UpdateEventRequestDto();

        eventDtoRequest.setCreatedAt(LocalDate.now().plusDays(1));
        eventDtoRequest.setEndsAt(LocalDate.now());

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bindingResult.hasErrors()).thenReturn(false);

        EventEntity event = Mockito.mock(EventEntity.class);

        assertThrows(BadRequestException.class, () -> {
            eventEntityService.updateEvent(id, eventDtoRequest, bindingResult);
        });

        verify(eventEntityRepository,never()).saveAndFlush(event);

    }

    @Test
    void updateProduct_ValidationError_ReturnBadRequestException() {
        Long id = 1L;
        Long userId = 1L;


        UpdateEventRequestDto eventDto =mock(UpdateEventRequestDto.class);
        when(eventDto.getTitle()).thenReturn("u");
        when(eventDto.getInformation()).thenReturn("w");

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new UserEntity()));
        when(bindingResult.hasErrors()).thenReturn(true);
        when(validationErrors.getValidationErrors(any(BindingResult.class))).thenReturn(("Ошибки валидации: имя слишком короткое, информация слишком короткая"));


        assertThrows(BadRequestException.class, () -> {
            eventEntityService.updateEvent(id,eventDto, bindingResult);
        });

        verify(eventEntityRepository, never()).saveAndFlush(any());

    }

    @Test
    void findEventByUser_FindEventsByUserId_ReturnEventResponseDtoWithEvents() {

        Long userId = 1L;
        EventEntity eventOne = new EventEntity();
        eventOne.setTitle("Event 1");

        EventEntity eventTwo = new EventEntity();
        eventTwo.setTitle("Event 2");;


        List<EventEntity> listEvent = Arrays.asList(eventOne, eventTwo);

        when(eventEntityRepository.findAllByUserList_UserId(userId)).thenReturn(listEvent);

        EventResponseDto eventResponseDtoOne =  EventResponseDto.builder()
                .title("Event 1")
                .build();

        EventResponseDto eventResponseDtoTwo = EventResponseDto.builder()
                .title("Event 2")
                .build();

        when(eventDtoFactory.makeEventDto(eventOne)).thenReturn(eventResponseDtoOne);
        when(eventDtoFactory.makeEventDto(eventTwo)).thenReturn(eventResponseDtoTwo);

        List<EventResponseDto> result = eventEntityService.findEventByUser(userId);

        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getTitle());
        assertEquals("Event 2", result.get(1).getTitle());

        verify(eventEntityRepository, times(1)).findAllByUserList_UserId(userId);
    }

    @Test
    void findEventByUser_FindEventsByUserId_ReturnEmptyCollections() {
        Long userId = 1L;

        when(eventEntityRepository.findAllByUserList_UserId(userId)).thenReturn(Collections.emptyList());

        List<EventResponseDto> result = eventEntityService.findEventByUser(userId);

        assertTrue(result.isEmpty());
        verify(eventEntityRepository, times(1)).findAllByUserList_UserId(userId);

    }

    @Test
    void deleteEvent_FindEventByIdForDelete_DeleteEvent() {
        Long id = 1L;
        EventEntity eventEntity =mock(EventEntity.class);
        when(controllerHelper.getEventOrThrowException(id)).thenReturn(eventEntity);

        eventEntityService.deleteProduct(id);

        verify(controllerHelper).getEventOrThrowException(id);
    }

}
