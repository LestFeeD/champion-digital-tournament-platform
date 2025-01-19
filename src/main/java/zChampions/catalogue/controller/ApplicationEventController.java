package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.requestDto.UpdateApplicationDtoRequest;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.requestDto.UpdateApplicationEventCreateDto;
import zChampions.catalogue.responseDto.ApplicationEventResponseDto;
import zChampions.catalogue.service.ApplicationEventService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "class working with event application")
@RestController
@RequiredArgsConstructor
@Transactional
public class ApplicationEventController {

    private final ApplicationEventService applicationEventService;

    public static final String SUBMIT_APPLICATION = "/add-registration/events/{event_id}";
    public static final String CANCEL_APPLICATION = "/events/{event_id}/applications";
    public static final String APPROVE_APPLICATION = "/manage-registration/events/{event_id}/applications/approve";
    public static final String REJECT_APPLICATION = "/manage-registration/events/{event_id}/applications/reject";
    public static final String ALL_APPLICATION = "/manage-registration/{event_id}";



    @Operation(
            summary = "Submits an application in the event",
            description = "Receives the dto, then transmits it to the service, where it is collected by the builder and saved to the database."
    )
    @PostMapping(SUBMIT_APPLICATION)
    public ApplicationEventResponseDto submitApplication(@PathVariable("event_id") Long eventId, @RequestBody UpdateApplicationEventCreateDto applicationEventCreateDto) throws BadRequestException {

        return  applicationEventService.submitApplication( eventId, applicationEventCreateDto);
    }

    @Operation(
            summary = "Cancellation of the application for the event",
            description = "Receives the dto, and then transmits it to the service, where the required application is deleted."
    )
    @DeleteMapping(CANCEL_APPLICATION)
    public void cancelApplication(@PathVariable("event_id") Long eventId, @RequestBody UpdateApplicationDtoRequest applicationEventDto) throws AccessDeniedException {

        applicationEventService.cancelApplication(eventId, applicationEventDto);
    }

    @Operation(
            summary = "Acceptance of the application for the event by the organizer",
            description = "Receives the dto, after which it transmits it to the service, " +
                    "in which the required application receives the APPROVED status and is saved in the database, along with the user."
    )
    @PatchMapping(APPROVE_APPLICATION)
    public ApplicationEventResponseDto approveApplication(@PathVariable("event_id") Long eventId, @RequestBody UpdateApplicationDtoRequest applicationEventDto) throws RoleNotAllowedException {

        return  applicationEventService.approveApplication(eventId, applicationEventDto);
    }
    @Operation(
            summary = "Refusal by the organizer of the application for participation in the event",
            description = "Receives the dto, after which it transmits it to the service, " +
                    "where the required application gets the REJECTED status and is saved in the database."
    )
    @PatchMapping(REJECT_APPLICATION)
    public void rejectApplication(@PathVariable("event_id") Long eventId, @RequestBody UpdateApplicationDtoRequest applicationEventDto) throws RoleNotAllowedException {

        applicationEventService.rejectApplication(eventId, applicationEventDto);
    }

    @Operation(
            summary = "Receiving all applications for a specific event.",
            description = "Receiving all the applications of the event, which can be viewed by the organizer."
    )
    @GetMapping(ALL_APPLICATION)
    public List<ApplicationEventResponseDto> getApplicationsForEvent(@PathVariable("event_id") Long eventId) throws RoleNotAllowedException {

        return applicationEventService.getApplicationsForEvent(eventId);
    }
}
