package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationDtoRequest;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationInteractionDto;
import zChampions.catalogue.responseDto.ApplicationOrganizationResponseDto;
import zChampions.catalogue.service.ApplicationOrganizationService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "class working with organization application")
@RestController
@RequiredArgsConstructor
@Transactional
public class ApplicationOrganizationController {

    private final ApplicationOrganizationService applicationOrganizationService;

    public static final String SUBMIT_APPLICATION = "/add-registration/organizations/{organization_id}";
    public static final String CANCEL_APPLICATION = "/organizations/{organization_id}/applications";
    public static final String APPROVE_APPLICATION = "/organizations/manage-registration/{organization_id}/applications/approve";
    public static final String REJECT_APPLICATION = "/organizations/manage-registration/{organization_id}/applications/reject";
    public static final String ALL_APPLICATION_SPORTSMEN = "/manage-registration/organizations/{organization_id}/sportsmen";
    public static final String ALL_APPLICATION_COACHES = "/manage-registration/organizations/{organization_id}/coaches";





    @Operation(
            summary = "Submits an application in the organization",
            description = "Receives the dto, then transmits it to the service, where it is collected by the builder and saved to the database."
    )
    @PostMapping(SUBMIT_APPLICATION)
    public ApplicationOrganizationResponseDto submitApplication(@PathVariable("organization_id") Long organizationId, @RequestBody UpdateApplicationOrganizationDtoRequest applicationOrganizationDtoRequest) {

        return  applicationOrganizationService.submitApplicationToTheOrganization(organizationId, applicationOrganizationDtoRequest);
    }
    @Operation(
            summary = "Cancellation of the application for the organization",
            description = "Receives the dto, and then transmits it to the service, where the required application is deleted."
    )
    @DeleteMapping(CANCEL_APPLICATION)
    public void cancelApplication(@PathVariable("organization_id") Long organizationId, @RequestBody UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws AccessDeniedException {

        applicationOrganizationService.cancelApplication(organizationId, organizationInteractionDto);
    }

    @Operation(
            summary = "Acceptance of the application for the organization by the organizer",
            description = "Receives the dto, after which it transmits it to the service, " +
                    "in which the required application receives the APPROVED status and is saved in the database, along with the user."
    )
    @PatchMapping(APPROVE_APPLICATION)
    public ApplicationOrganizationResponseDto approveApplication(@PathVariable("organization_id") Long organizationId, @RequestBody UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws RoleNotAllowedException {

        return  applicationOrganizationService.approveApplication(organizationId, organizationInteractionDto);
    }
    @Operation(
            summary = "Refusal by the organizer of the application for participation in the organization",
            description = "Receives the dto, after which it transmits it to the service, " +
                    "where the required application gets the REJECTED status and is saved in the database."
    )
    @PatchMapping(REJECT_APPLICATION)
    public void rejectApplication(@PathVariable("organization_id") Long organizationId, @RequestBody UpdateApplicationOrganizationInteractionDto organizationInteractionDto) throws RoleNotAllowedException {

        applicationOrganizationService.rejectApplication(organizationId, organizationInteractionDto);
    }
    @Operation(
            summary = "All applications with the role of an Athlete of a certain organization.",
            description = "Receives the organization's ID, sends the service, and receives a DTO response"
    )
    @GetMapping(ALL_APPLICATION_SPORTSMEN)
    public List<ApplicationOrganizationResponseDto> getSportsmenApplicationsApplicationsForOrganization(@PathVariable("organization_id") Long organizationId)
            throws RoleNotAllowedException {

        return applicationOrganizationService.getSportsmenApplicationsForOrganization(organizationId);
    }
    @Operation(
            summary = "All applications with the role of an Coach of a certain organization.",
            description = "Receives the organization's ID, sends the service, and receives a DTO response"
    )
    @GetMapping(ALL_APPLICATION_COACHES)
    public List<ApplicationOrganizationResponseDto> getCoachesApplicationsForOrganization(@PathVariable("organization_id") Long organizationId) throws RoleNotAllowedException {

        return applicationOrganizationService.getCoachesApplicationsForOrganization(organizationId);
    }


}
