package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.requestDto.*;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationDomainRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationContactSettingsRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationGeneralSettingsRequestDto;
import zChampions.catalogue.responseDto.AllOrganizationResponseDto;
import zChampions.catalogue.responseDto.OrganizationCreateResponseDto;
import zChampions.catalogue.responseDto.UserInOrganizationResponseDto;
import zChampions.catalogue.service.OrganizationEntityService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "class working with organization")
@RestController
@RequiredArgsConstructor
@Transactional
public class OrganizationController {

    private final OrganizationEntityService organizationEntityService;

    public static final String FETCH_ORGANIZATION = "/fetch-organizations";
    public static final String USER_ORGANIZATIONS = "/cabinet/{user_id}/organizations";
    public static final String POST_ORGANIZATION = "/add-organizations";
    public static final String POST_DOMAIN_ORGANIZATION = "/edit-options/add-domain/organizations/{organization_id}";
    public static final String All_ORGANIZATION = "/all-organizations";
    public static final String EDIT_GENERAL_ORGANIZATION = "/edit-options/organizations/{organization_id}/general";
    public static final String EDIT_CONTACTS_ORGANIZATION = "/edit-options/organizations/{organization_id}/contacts";
    public static final String DELETE_ORGANIZATION = "/edit-options/delete/organizations/{event_id}";
    public static final String ALL_PARTICIPANTS_SPORTSMEN = "/organizations/{organization_id}/all-participants";
    public static final String ALL_PARTICIPANTS_COACHES = "/organizations/{organization_id}/all-coaches";
    public static final String DELETE_USER_ORGANIZATION = "/organizations/{organization_id}/users/{user_id}";

    @Operation(
            summary = "Find all the system events.",
            description = "Finds all organizations through the repositories and sends the DTO."
    )
    @GetMapping(All_ORGANIZATION)
    public List<AllOrganizationResponseDto> getAllOrganization() {
        return organizationEntityService.findOfAllOrganizations();
    }

    @Operation(
            summary = "Find an organization by parameters.",
            description = "Receives the specified parameters, passes them to the service, which accesses the repository from which it retrieves the data."
    )
    @GetMapping(FETCH_ORGANIZATION)
    public List<AllOrganizationResponseDto> sortedOrganization(@RequestBody UpdateOrganizationFetchRequestDto requestDto) {

        return  organizationEntityService.findByParameters(requestDto);
    }

    @Operation(
            summary = "Find all the user's organization.",
            description = "Retrieves the user ID, passes it to the service, which accesses the repository and retrieves all organizations based on the user ID."
    )
    @GetMapping(USER_ORGANIZATIONS)
    public List<AllOrganizationResponseDto> getUserOrganizations(@PathVariable(value = "user_id") Long userId) {
        return organizationEntityService.findOrganizationByUser(userId);
    }

    @Operation(
            summary = "Creating an organization.",
            description = "It receives a DTO with parameters and passes it to the service, which creates an organization with the specified parameters and returns the result as a DTO."
    )
    @PostMapping(POST_ORGANIZATION)
    public OrganizationCreateResponseDto createOrganization(@RequestBody CreateOrganizationRequestDto organizationDtoRequest, BindingResult bindingResult)  throws BadRequestException {


        return organizationEntityService.createOrganization( organizationDtoRequest,  bindingResult);

    }

    @Operation(
            summary = "Editing the main data of an organization.",
            description = "Receives data from the main data for the organization, sends it to the service and updates the specified organization."
    )
    @PatchMapping(EDIT_GENERAL_ORGANIZATION)
    public OrganizationCreateResponseDto updateGeneralSettingsOrganization(@PathVariable("organization_id") Long organizationId,
                                                                   @RequestBody @Valid UpdateOrganizationGeneralSettingsRequestDto organizationGeneralSettingsRequestDto,
                                                                   BindingResult bindingResult) throws BadRequestException, RoleNotAllowedException, AccessDeniedException {


        return organizationEntityService.updateGeneralSettingsOrganization(organizationId, organizationGeneralSettingsRequestDto, bindingResult);
    }

    @Operation(
            summary = "Editing the organization's contact information.",
            description = "Receives the data with the contact details, sends it to the service and updates the specified organization.."
    )
    @PatchMapping(EDIT_CONTACTS_ORGANIZATION)
    public OrganizationCreateResponseDto updateContactOrganization(@PathVariable("organization_id") Long organizationId,
                                                           @RequestBody @Valid UpdateOrganizationContactSettingsRequestDto organizationContactSettings) throws BadRequestException, AccessDeniedException, RoleNotAllowedException {


        return organizationEntityService.updateContactSettingsOrganization(organizationId, organizationContactSettings);
    }

    @Operation(
            summary = "Editing the organization's domain information.",
            description = "Receives the data with the domain, sends it to the service and updates the specified organization.."
    )
    @PatchMapping(POST_DOMAIN_ORGANIZATION)
    public OrganizationCreateResponseDto updateDomainOrganization(@PathVariable("organization_id") Long organizationId,
                                                          @RequestBody CreateOrganizationDomainRequestDto organizationDtoRequest) throws RoleNotAllowedException{


        return organizationEntityService.createDomainOrganization(organizationId,organizationDtoRequest);
    }

    @Operation(
            summary = "Gets all the athletes in the organization",
            description = "It receives the organization's ID by transferring it to the service, and after accessing the repository, it returns the result to the users and transmits it to the DTO."
    )
    @GetMapping(ALL_PARTICIPANTS_SPORTSMEN)
    public List<UserInOrganizationResponseDto> getSportsmenForOrganization(@PathVariable("organization_id") Long organizationId) throws BadRequestException {

        return organizationEntityService.getSportsmenUsersInOrganization(organizationId);
    }
    @Operation(
            summary = "Gets all the coaches in the organization",
            description = "It receives the organization's ID by transferring it to the service, and after accessing the repository, " +
                    "it returns the result to the users and transmits it to the DTO."
    )
    @GetMapping(ALL_PARTICIPANTS_COACHES)
    public List<UserInOrganizationResponseDto> getCoachesOrganization(@PathVariable("organization_id") Long organizationId) throws BadRequestException  {

        return organizationEntityService.getCoachUsersInOrganization(organizationId);
    }

    @Operation(
            summary = "Removing a user from an organization.",
            description = "Gets the ID of the organization and the user to delete."
    )
    @DeleteMapping(DELETE_USER_ORGANIZATION)
    public void deleteUserOrganization(@PathVariable("organization_id") Long organizationId, @PathVariable("user_id") Long userId) throws RoleNotAllowedException {
        organizationEntityService.deleteUserInOrganization(organizationId, userId);
    }

    @Operation(
            summary = "Deleting an organization.",
            description = "Gets the organization's ID and deletes it."
    )
    @DeleteMapping(DELETE_ORGANIZATION)
    public void deleteOrganization(@PathVariable("organization_id")Long id) {
        organizationEntityService.deleteOrganization(id);

    }
}
