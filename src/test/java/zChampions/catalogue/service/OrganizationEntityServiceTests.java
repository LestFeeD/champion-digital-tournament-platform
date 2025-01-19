package zChampions.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.ApplicationOrganization;
import zChampions.catalogue.entity.OrganizationEntity;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.entity.UserOrganizationRole;
import zChampions.catalogue.enumsEntities.KindOfSport;
import zChampions.catalogue.enumsEntities.RoleSport;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.factories.AllOrganizationEntityDtoFactory;
import zChampions.catalogue.factories.OrganizationEntityDtoFactory;
import zChampions.catalogue.repository.ApplicationOrganizationRepository;
import zChampions.catalogue.repository.OrganizationRepository;
import zChampions.catalogue.repository.UserOrganizationRoleRepository;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.requestDto.UpdateOrganizationFetchRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationDomainRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateOrganizationRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationContactSettingsRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateOrganizationGeneralSettingsRequestDto;
import zChampions.catalogue.responseDto.AllOrganizationResponseDto;
import zChampions.catalogue.responseDto.OrganizationCreateResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizationEntityServiceTests {

    @Mock
    private  OrganizationRepository organizationEntityRepository;
    @Mock
    private  OrganizationEntityDtoFactory organizationEntityDtoFactory;
    @Mock
    private  AllOrganizationEntityDtoFactory allOrganizationEntityDtoFactory;
    @Mock
    private  ControllerHelper controllerHelper;
    @Mock
    private  UserRepository userEntityRepository;
    @Mock
    private  UserOrganizationRoleRepository userOrganizationRoleRepository;
    @Mock
    private  ValidationErrors validationErrors;
    @Mock
    private  AuthenticationService authenticationService;

    @Mock
    private  ApplicationOrganizationRepository applicationOrganizationRepository;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private  UserRoleChecker userRoleChecker;

    @InjectMocks
    private OrganizationEntityService organizationEntityService;

    @Test
    void findOfAllOrganizations_findAllOrganization_returnListAllOrganizationResponseDto() {

        OrganizationEntity organizationOne = new OrganizationEntity();

        OrganizationEntity organizationTwo = new OrganizationEntity();

        List<OrganizationEntity> listOrganization = new ArrayList<>();
        listOrganization.add(organizationOne);
        listOrganization.add(organizationTwo);

        AllOrganizationResponseDto dtoOne = mock(AllOrganizationResponseDto.class);
        AllOrganizationResponseDto dtoTwo = mock(AllOrganizationResponseDto.class);

        when(organizationEntityRepository.findAll()).thenReturn(listOrganization);
        when(allOrganizationEntityDtoFactory.makeOrganizations(organizationOne)).thenReturn(dtoOne);
        when(allOrganizationEntityDtoFactory.makeOrganizations(organizationTwo)).thenReturn(dtoTwo);

        List<AllOrganizationResponseDto> result = organizationEntityService.findOfAllOrganizations();


        assertEquals(2, result.size(), "Result list size should match");
        verify(allOrganizationEntityDtoFactory, times(1)).makeOrganizations(organizationOne);
        verify(allOrganizationEntityDtoFactory, times(1)).makeOrganizations(organizationTwo);

    }

    @Test
    void findByParameters_findOrganizationWithParametersTitleAndCity_returnListAllOrganizationResponseDto() {
        String title = "testTitle";
        String region = "testRegion";

        UpdateOrganizationFetchRequestDto organizationFetchRequestDto = new UpdateOrganizationFetchRequestDto();
        organizationFetchRequestDto.setTitle(title);
        organizationFetchRequestDto.setRegion(region);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTitle(title);
        organization.setCity(region);

        List<OrganizationEntity> listOrganization = new ArrayList<>();
        listOrganization.add(organization);
        when(organizationEntityRepository.findByTitleAndRegion(title, region)).thenReturn(listOrganization);

        organizationEntityService.findByParameters(organizationFetchRequestDto);

        verify(organizationEntityRepository, times(1)).findByTitleAndRegion(title, region);
        verify(allOrganizationEntityDtoFactory, times(1)).makeOrganizations(organization);
    }

    @Test
    void findByParameters_findOrganizationWithParametersTitle_returnOListAllOrganizationResponseDto() {
        String title = "testTitle";

        UpdateOrganizationFetchRequestDto organizationFetchRequestDto = new UpdateOrganizationFetchRequestDto();
        organizationFetchRequestDto.setTitle(title);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setTitle(title);

        List<OrganizationEntity> listOrganization = new ArrayList<>();
        listOrganization.add(organization);
        when(organizationEntityRepository.findByTitle(title)).thenReturn(listOrganization);

        organizationEntityService.findByParameters(organizationFetchRequestDto);

        verify(organizationEntityRepository, times(1)).findByTitle(title);
    }



    @Test
    void findOrganizationByUser_findOrganizationInCabinetByUser_returnListAllOrganizationResponseDto() {

        Long userId = 1L;
        OrganizationEntity organization = new OrganizationEntity();

        when(organizationEntityRepository.findAllByUsers_UserId(userId)).thenReturn(Collections.singletonList(organization));

        organizationEntityService.findOrganizationByUser(userId);

        verify(organizationEntityRepository, times(1 )).findAllByUsers_UserId(userId);
        verify(allOrganizationEntityDtoFactory, times(1)).makeOrganizations(organization);

    }


    @Test
    void createOrganization_createOrganization_returnOrganizationCreateResponseDto() throws BadRequestException {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateOrganizationRequestDto organizationDtoRequest = new CreateOrganizationRequestDto();
        organizationDtoRequest.setTitle("testTitle");
        organizationDtoRequest.setCountry("testCountry");
        organizationDtoRequest.setKindOfSport(KindOfSport.BASKETBALL);
        organizationDtoRequest.setRegion("testRegion");
        organizationDtoRequest.setCity("testCity");
        organizationDtoRequest.setEmail("testEmail");
        organizationDtoRequest.setPhoneNumber("981");

        OrganizationEntity organization = OrganizationEntity.builder()
                .title(organizationDtoRequest.getTitle())
                .country(organizationDtoRequest.getCountry())
                .kindOfSport(organizationDtoRequest.getKindOfSport())
                .region(organizationDtoRequest.getRegion())
                .city(organizationDtoRequest.getCity())
                .email(organizationDtoRequest.getEmail())
                .phoneNumber(organizationDtoRequest.getPhoneNumber())
                .users(new HashSet<>(Collections.singletonList(user)))
                .build();

        UserOrganizationRole userRole = UserOrganizationRole.builder()
                .user(user)
                .organization(organization)
                .role(RoleSport.ORGANIZER)
                .build();

        user.setOrganizationEntityList(new HashSet<>(Collections.singletonList(organization)));
        user.setUserOrganizationRoles(Collections.singletonList(userRole));

        OrganizationCreateResponseDto dtoOne = mock(OrganizationCreateResponseDto.class);


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(organizationEntityRepository.save(ArgumentMatchers.any(OrganizationEntity.class))).thenReturn(organization);
        when(userEntityRepository.save(user)).thenReturn(user);

        organizationEntityService.createOrganization(organizationDtoRequest, bindingResult);

        verify(organizationEntityRepository, times(1)).save(ArgumentMatchers.any(OrganizationEntity.class));
        verify(userEntityRepository, times(1)).save(user);
        verify(userOrganizationRoleRepository, times(1)).save(any(UserOrganizationRole.class));
    }

    @Test
    void createOrganization_createOrganizationWithValidationErrors_returnOBadRequestException() throws BadRequestException {

        UserEntity user = new UserEntity();
        user.setUserId(1L);

        CreateOrganizationRequestDto organizationDtoRequest = new CreateOrganizationRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(user.getUserId());
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            organizationEntityService.createOrganization(organizationDtoRequest, bindingResult);
        });

        verify(organizationEntityRepository, never()).saveAndFlush(any(OrganizationEntity.class));
        verify(userEntityRepository, never()).saveAndFlush(any(UserEntity.class));
        verify(userOrganizationRoleRepository, never()).saveAndFlush(any(UserOrganizationRole.class));
        verify(organizationEntityDtoFactory,never()).makeOrganization(any(OrganizationEntity.class));

    }

    @Test
    void createDomainOrganization_createDomainForOrganization_returnOrganizationCreateResponseDto() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateOrganizationDomainRequestDto organizationDtoRequest = new CreateOrganizationDomainRequestDto();
        organizationDtoRequest.setLinkWebsite("test");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        user.setOrganizationEntityList(new HashSet<>(Collections.singletonList(organization)));
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));

        when(authenticationService.getCurrentUserId()).thenReturn(user.getUserId());
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(organizationEntityRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(organizationEntityRepository.saveAndFlush(organization)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        organizationEntityService.createDomainOrganization(organizationId, organizationDtoRequest);

        verify(organizationEntityRepository, times(1)).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, times(1)).makeOrganization(organization);


    }

    @Test
    void createDomainOrganization_UserIsNoTheOrganizer_returnRoleNotAllowedException() {
        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();

        CreateOrganizationDomainRequestDto organizationDtoRequest = new CreateOrganizationDomainRequestDto();
        organizationDtoRequest.setLinkWebsite("test");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setUsers(new HashSet<>(Collections.singletonList(new UserEntity())));


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(organizationEntityRepository.findById(organizationId)).thenReturn(Optional.of(organization));

        assertThrows(RoleNotAllowedException.class, () -> organizationEntityService.createDomainOrganization(organizationId, organizationDtoRequest));

        verify(organizationEntityRepository,never()).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, never()).makeOrganization(organization);

    }

    @Test
    void updateGeneralSettingsOrganization_updateOrganizationWithParameterTitle_returnOrganizationCreateResponseDto() throws AccessDeniedException, BadRequestException, RoleNotAllowedException {

        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateOrganizationGeneralSettingsRequestDto requestDto = new UpdateOrganizationGeneralSettingsRequestDto();
        requestDto.setTitle("new title");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setTitle("title");


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);
        when(organizationEntityRepository.saveAndFlush(organization)).thenReturn(organization);

        organizationEntityService.updateGeneralSettingsOrganization(organizationId, requestDto, bindingResult);

        verify(authenticationService, times(1)).getCurrentUserId();
        verify(organizationEntityRepository, times(1)).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, times(1)).makeOrganization(organization);

    }

    @Test
    void updateGeneralSettingsOrganization_updateOrganizationWithValidationErrors_returnOBadRequestException() {
        Long organizationId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(1L);

        UpdateOrganizationGeneralSettingsRequestDto organizationDtoRequest = new UpdateOrganizationGeneralSettingsRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(user.getUserId());
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            organizationEntityService.updateGeneralSettingsOrganization(organizationId, organizationDtoRequest, bindingResult);
        });

        verify(organizationEntityRepository, never()).saveAndFlush(any(OrganizationEntity.class));
        verify(organizationEntityDtoFactory,never()).makeOrganization(any(OrganizationEntity.class));
    }

    @Test
    void updateGeneralSettingsOrganization_UserIsNoTheOrganizer_returnRoleNotAllowedException() {
        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();

        UpdateOrganizationGeneralSettingsRequestDto organizationDtoRequest = new UpdateOrganizationGeneralSettingsRequestDto();


        OrganizationEntity organization = new OrganizationEntity();


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> organizationEntityService.updateGeneralSettingsOrganization(organizationId, organizationDtoRequest, bindingResult));

        verify(organizationEntityRepository,never()).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, never()).makeOrganization(organization);

    }

    @Test
    void updateContactSettingsOrganization_updateOrganizationWithParameterEmail_returnOrganizationCreateResponseDto() throws AccessDeniedException, BadRequestException, RoleNotAllowedException {

        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateOrganizationContactSettingsRequestDto requestDto = new UpdateOrganizationContactSettingsRequestDto();
        requestDto.setEmail("new email");

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setTitle("email");


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);
        when(organizationEntityRepository.saveAndFlush(organization)).thenReturn(organization);

        organizationEntityService.updateContactSettingsOrganization(organizationId, requestDto);

        verify(authenticationService, times(1)).getCurrentUserId();
        verify(organizationEntityRepository, times(1)).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, times(1)).makeOrganization(organization);

    }

    @Test
    void updateContactSettingsOrganization_UserIsNoTheOrganizer_returnRoleNotAllowedException() {
        Long userId = 1L;
        Long organizationId = 1L;

        UserEntity user = new UserEntity();

        UpdateOrganizationContactSettingsRequestDto organizationDtoRequest = new UpdateOrganizationContactSettingsRequestDto();


        OrganizationEntity organization = new OrganizationEntity();

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> organizationEntityService.updateContactSettingsOrganization(organizationId, organizationDtoRequest));

        verify(organizationEntityRepository,never()).saveAndFlush(organization);
        verify(organizationEntityDtoFactory, never()).makeOrganization(organization);

    }

    @Test
    void getSportsmenUsersInOrganization_getSportsmen_returnListUserInOrganizationResponseDtoWithSportsmen() throws BadRequestException {

        UserOrganizationRole userOrganizationRole = new UserOrganizationRole();
        userOrganizationRole.setRole(RoleSport.SPORTSMAN);

        UserEntity userOne = new UserEntity();
        userOne.setFirstName("testName1");
        userOne.setUserOrganizationRoles(Collections.singletonList(userOrganizationRole));

        UserEntity userTwo = new UserEntity();
        userOne.setFirstName("testName2");
        userTwo.setUserOrganizationRoles(Collections.singletonList(userOrganizationRole));

        Set<UserEntity> userList = new HashSet<>();
        userList.add(userOne);
        userList.add(userTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setUsers(userList);

        when(controllerHelper.getOrganizationOrThrowException(1L)).thenReturn(organization);

        organizationEntityService.getSportsmenUsersInOrganization(1L);

        verify(controllerHelper, times(1)).getOrganizationOrThrowException(1L);

    }

    @Test
    void getSportsmenUsersInOrganization_getSportsmenWhereListIsEmpty_returnListUserInOrganizationResponseDtoWithSportsmen() throws BadRequestException {

        OrganizationEntity organization = new OrganizationEntity();
        organization.setUsers(new HashSet<>());

        when(controllerHelper.getOrganizationOrThrowException(1L)).thenReturn(organization);

        organizationEntityService.getSportsmenUsersInOrganization(1L);

        verify(controllerHelper, times(1)).getOrganizationOrThrowException(1L);

    }

    @Test
    void getCoachUsersInOrganization_getСoaches_returnListUserInOrganizationResponseDtoWithСoaches() throws BadRequestException {

        UserOrganizationRole userOrganizationRole = new UserOrganizationRole();
        userOrganizationRole.setRole(RoleSport.COACH);

        UserEntity userOne = new UserEntity();
        userOne.setFirstName("testName1");
        userOne.setUserOrganizationRoles(Collections.singletonList(userOrganizationRole));

        UserEntity userTwo = new UserEntity();
        userOne.setFirstName("testName2");
        userTwo.setUserOrganizationRoles(Collections.singletonList(userOrganizationRole));

        Set<UserEntity> userList = new HashSet<>();
        userList.add(userOne);
        userList.add(userTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setUsers(userList);

        when(controllerHelper.getOrganizationOrThrowException(1L)).thenReturn(organization);

        organizationEntityService.getCoachUsersInOrganization(1L);

        verify(controllerHelper, times(1)).getOrganizationOrThrowException(1L);

    }

    @Test
    void getSportsmenUsersInOrganization_getСoachesWhereListIsEmpty_returnListUserInOrganizationResponseDtoWithСoaches() throws BadRequestException {


        OrganizationEntity organization = new OrganizationEntity();
        organization.setUsers(new HashSet<>());

        when(controllerHelper.getOrganizationOrThrowException(1L)).thenReturn(organization);

        organizationEntityService.getCoachUsersInOrganization(1L);

        verify(controllerHelper, times(1)).getOrganizationOrThrowException(1L);

    }

    @Test
    void deleteUserInOrganization_deleteUser() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;
        Long applicationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        user.setApplicationOrganizations(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setUser(user);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setOrganization(organization);
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));
        user.setOrganizationEntityList(new HashSet<>(Collections.singletonList(organization)));


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        organizationEntityService.deleteUserInOrganization(organizationId, userId);

        verify(applicationOrganizationRepository, times(1)).delete(applicationOrganization);
        verify(organizationEntityRepository, times(1)).save(organization);
    }

    @Test
    void deleteUserInOrganization_deleteUser_returnRoleNotAllowedException() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;
        Long applicationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        user.setApplicationOrganizations(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setUser(user);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setOrganization(organization);


        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () ->  organizationEntityService.deleteUserInOrganization(organizationId, userId));

        verify(applicationOrganizationRepository, never()).delete(applicationOrganization);
        verify(organizationEntityRepository, never()).save(organization);
    }

    @Test
    void deleteUserInOrganization_deleteUser_ApplicationNotFound() throws NotFoundException {
        Long userId = 1L;
        Long organizationId = 1L;
        Long applicationId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        user.setApplicationOrganizations(new HashSet<>());

        OrganizationEntity organization = new OrganizationEntity();
        organization.setApplicationOrganizationSet(new HashSet<>());
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));
        user.setOrganizationEntityList(new HashSet<>(Collections.singletonList(organization)));

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        assertThrows(NotFoundException.class, () -> organizationEntityService.deleteUserInOrganization(organizationId, userId));

        verify(applicationOrganizationRepository, never()).delete(any(ApplicationOrganization.class));
        verify(organizationEntityRepository, never()).save(any(OrganizationEntity.class));
    }

    @Test
    void deleteOrganization_deleteOrganization(){
        Long organizationId = 1L;
        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);

        organizationEntityService.deleteOrganization(organizationId);

        verify(organizationEntityRepository, times(1)).deleteById(organizationId);
    }




}
