package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.requestDto.createRequest.CreateEventDtoRequest;
import zChampions.catalogue.requestDto.updateRequest.UpdateEventRequestDto;
import zChampions.catalogue.responseDto.EventResponseDto;
import zChampions.catalogue.responseDto.UserEventResponseDto;
import zChampions.catalogue.service.EventEntityService;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Tag(name = "class working with events")
@RestController
@RequiredArgsConstructor
@Transactional
public class EventEntityController {

    private final EventEntityService eventEntityService;
    private static final Logger logger = LoggerFactory.getLogger(EventEntityController.class);

    public static final String FETCH_EVENT = "/fetch-events";
    public static final String USER_IN_EVENT = "/users-events/events/{event_id}/users";
    public static final String USER_EVENT = "/cabinet/{user_id}/events";
    public static final String POST_EVENT = "/add-events";
    public static final String GET_EVENT = "/events/{event_id}";
    public static final String All_EVENTS = "/all-events";
    public static final String EDIT_EVENT = "/edit-options/events/{event_id}";
    public static final String DELETE_EVENT = "/edit-options/delete/events/{event_id}";
    public static final String DELETE_USER_EVENT = "/events/{event_id}/users/{user_id}";

    @Operation(
            summary = "Find all the system events.",
            description = "Finds all events through the repositories and sends the DTO."
    )
    @GetMapping(All_EVENTS)
    public List<EventResponseDto> getEvents() {
        return eventEntityService.findAllEvent();
    }

    @Operation(
            summary = "Receive a specific event",
            description = "Find a specific event by ID and sends a DTO."
    )
    @GetMapping(GET_EVENT)
    public EventResponseDto getAllEvent(@PathVariable("event_id") Long id) throws NotFoundException {
        return eventEntityService.findEvent(id);

    }

    @Operation(
            summary = "Find an event by parameters.",
            description = "Receives the specified parameters, passes them to the service, which accesses the repository from which it retrieves the data."
    )
    @GetMapping(FETCH_EVENT)
    public List<EventResponseDto> sortedEvents(@RequestParam(value = "city", required = false) String city,
                                             @RequestParam(value = "kindOfSport", required = false) KindOfSport kindOfSport) {

        logger.info("Fetching events with city: {} and kindOfSport: {}", city, kindOfSport);
        return  eventEntityService.findByParameters(city, kindOfSport);
    }

    @Operation(
            summary = "Finds all related users who are part of the event.",
            description = "Receives the event ID, passes it to the service, which accesses the repository and retrieves all users based on the event ID."
    )
    @GetMapping(USER_IN_EVENT)
    public List<UserEventResponseDto> usersInEvent(@PathVariable(value = "event_id") Long eventId) {
        return  eventEntityService.findUsersInEvent(eventId);
    }

    @Operation(
            summary = "Find all the user's event.",
            description = "Retrieves the user ID, passes it to the service, which accesses the repository and retrieves all events based on the user ID."
    )
    @GetMapping(USER_EVENT)
    public List<EventResponseDto> getUserOrganizations(@PathVariable(value = "user_id") Long userId) {
        return eventEntityService.findEventByUser(userId);
    }

    @Operation(
            summary = "Creating an event.",
            description = "It receives a DTO with parameters and passes it to the service, which creates an event with the specified parameters and returns the result as a DTO."
    )
    @PostMapping(POST_EVENT)
    public EventResponseDto createEvent(

            @RequestBody @Valid CreateEventDtoRequest eventCreateRequest, BindingResult bindingResult)  throws BadRequestException {

            EventResponseDto entity = eventEntityService.createEvent(
                eventCreateRequest,
                bindingResult);

        return entity;
    }

    @Operation(
            summary = "Event Editing",
            description = "Receives the event ID and DTO with the parameters, sends it to the service, and then the event is saved with the new parameters."
    )
    @PatchMapping(EDIT_EVENT)
    public ResponseEntity<?>  editEvent(@PathVariable("event_id") Long eventId,
                                        @Valid @RequestBody UpdateEventRequestDto eventEntityDto, BindingResult bindingResult) throws BadRequestException, AccessDeniedException {


        return eventEntityService.updateEvent(eventId, eventEntityDto, bindingResult);
    }

    @Operation(
            summary = "Remove a user from an event",
            description = "Receives the user's ID, transmits it to the service, and deletes the user and his application from the event."
    )
    @DeleteMapping(DELETE_USER_EVENT)
    public void deleteUserEvent(@PathVariable("event_id") Long eventId, @PathVariable("user_id") Long userId) throws RoleNotAllowedException {
        eventEntityService.deleteUserInEvent(eventId, userId);
    }

    @Operation(
            summary = "Deleting an event",
            description = "Receives the ID of the event, sends it to the service, and then the event is deleted."
    )
    @DeleteMapping(DELETE_EVENT)
    public void deleteEvent(@PathVariable("event_id")Long id) {
        eventEntityService.deleteProduct(id);

    }

}
