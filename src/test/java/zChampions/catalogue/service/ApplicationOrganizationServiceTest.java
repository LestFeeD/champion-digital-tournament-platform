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
import zChampions.catalogue.exceptions.DuplicateApplicationException;
import zChampions.catalogue.exceptions.IllegalArgumentException;
import zChampions.catalogue.exceptions.RoleNotAllowedException;
import zChampions.catalogue.factories.ApplicationOrganizationFactory;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationDtoRequest;
import zChampions.catalogue.requestDto.UpdateApplicationOrganizationInteractionDto;
import zChampions.catalogue.responseDto.ApplicationOrganizationResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationOrganizationServiceTest {
    @Mock
    private ControllerHelper controllerHelper;
    @Mock
    private UserRepository userEntityRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ApplicationOrganizationRepository applicationRepository;
    @Mock
    private ApplicationOrganizationFactory applicationFactory;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserOrganizationRoleRepository userOrganizationRoleRepository;
    @Mock
    private  UserRoleChecker userRoleChecker;


    @InjectMocks
    private ApplicationOrganizationService applicationOrganizationService;

    @Test
    void submitApplicationToTheOrganization_withValidRequestWithSportsmanRole_returnApplicationOrganizationResponseDto(){
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);


        Long organizationId = 1L;
        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(new HashSet<>());

        ApplicationOrganizationResponseDto expectedResponseDto = ApplicationOrganizationResponseDto.builder().build();

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));

        ApplicationOrganization applicationOrganization = ApplicationOrganization.builder()
                .user(user)
                .status(Status.PENDING)
                .build();
        user.setApplicationOrganizations(new HashSet<>(Collections.singletonList(applicationOrganization)));


        UpdateApplicationOrganizationDtoRequest applicationOrganizationDtoRequest = new UpdateApplicationOrganizationDtoRequest();
        applicationOrganizationDtoRequest.setRoleSport(RoleSport.SPORTSMAN);

        when(applicationRepository.save(any(ApplicationOrganization.class))).thenReturn(applicationOrganization);
        when(userEntityRepository.save(user)).thenReturn(user);
        when(organizationRepository.save(organization)).thenReturn(organization);
        when(applicationFactory.makeApplicationOrganizationDto(any(ApplicationOrganization.class))).thenReturn(expectedResponseDto);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(applicationRepository.existsByOrganization_organizationIdAndUser_userId(organizationId, userId))
                .thenReturn(false);

        ApplicationOrganizationResponseDto actualResponseDto = applicationOrganizationService.submitApplicationToTheOrganization(
                organizationId, applicationOrganizationDtoRequest);

        assertEquals(expectedResponseDto, actualResponseDto);

        verify(applicationRepository, times(1)).save(any(ApplicationOrganization.class));
        verify(userEntityRepository, times(1)).save(user);
        verify(organizationRepository, times(1)).save(organization);
        verify(applicationFactory, times(1)).makeApplicationOrganizationDto(any(ApplicationOrganization.class));
    }

    @Test
    void submitApplicationToTheOrganization_ApplicationAlreadySubmitted_returnDuplicateApplicationException() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        Long organizationId = 1L;
        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);


        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(applicationRepository.existsByOrganization_organizationIdAndUser_userId(organizationId, userId))
                .thenReturn(true);

        UpdateApplicationOrganizationDtoRequest applicationOrganizationDtoRequest = new UpdateApplicationOrganizationDtoRequest();
        applicationOrganizationDtoRequest.setRoleSport(RoleSport.SPORTSMAN);


        assertThrows(DuplicateApplicationException.class, () -> {
            applicationOrganizationService.submitApplicationToTheOrganization(organizationId, applicationOrganizationDtoRequest);
        });

        verify(applicationRepository, never()).save(any(ApplicationOrganization.class));
        verify(organizationRepository,never()).save(organization);
        verify(userEntityRepository, never()).save(user);
    }
    @Test
    void submitApplicationToTheOrganization_invalidRoleForInApplication_returnIllegalArgumentException() {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        Long organizationId = 1L;
        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);


        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(user));
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(applicationRepository.existsByOrganization_organizationIdAndUser_userId(organizationId, userId))
                .thenReturn(false);

        UpdateApplicationOrganizationDtoRequest applicationOrganizationDtoRequest = new UpdateApplicationOrganizationDtoRequest();

        assertThrows(IllegalArgumentException.class, () -> {
            applicationOrganizationService.submitApplicationToTheOrganization(organizationId, applicationOrganizationDtoRequest);
        });

        verify(applicationRepository, never()).save(any(ApplicationOrganization.class));
        verify(organizationRepository,  never()).save(organization);
        verify(userEntityRepository,  never()).save(user);
    }

    @Test
    void cancelApplication_deleteApplicationRequest_DeleteByIdApplicationInOrganization() throws AccessDeniedException {

        Long applicationId = 1L;
        Long organizationId = 1L;
        Long userId = 1L;

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);


        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        applicationOrganization.setUser(userEntity);

        userEntity.setApplicationOrganizations(new HashSet<>(Collections.singletonList(applicationOrganization)));
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));

        UpdateApplicationOrganizationInteractionDto applicationOrganizationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationOrganizationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(applicationRepository.existsById(applicationId)).thenReturn(true);


        applicationOrganizationService.cancelApplication(organizationId, applicationOrganizationDtoRequest);

        verify(applicationRepository, times(1)).deleteById(applicationId);

        verify(userEntityRepository, times(1)).save(userEntity);

    }

    @Test
    void cancelApplication_userExists_returnAccessDeniedException() throws AccessDeniedException {

        Long applicationId = 1L;
        Long organizationId = 1L;
        Long anotherUserId = 1L;

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(2L);

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        applicationOrganization.setUser(userEntity);

        UpdateApplicationOrganizationInteractionDto applicationOrganizationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationOrganizationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(anotherUserId);


        assertThrows(AccessDeniedException.class, () -> {
            applicationOrganizationService.cancelApplication(organizationId, applicationOrganizationDtoRequest);
        });

        verify(applicationRepository, times(0)).deleteById(applicationId);

        verify(userEntityRepository, times(0)).save(userEntity);
    }

    @Test
    void approveApplication_approveApplicationInEventByOrganizer_returnDtoApplication() throws RoleNotAllowedException {
        Long applicationId = 1L;
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setStatus(Status.PENDING);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setOrganization(organization);

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        organization.setUsers(new HashSet<>(Collections.singletonList(user)));
        user.setOrganizationEntityList(new HashSet<>(Collections.singletonList(organization)));
        user.setApplicationOrganizations(new HashSet<>(Collections.singletonList(applicationOrganization)));
        applicationOrganization.setUser(user);

        UpdateApplicationOrganizationInteractionDto applicationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);


        applicationOrganizationService.approveApplication(organizationId, applicationDtoRequest);

        assertEquals(Status.APPROVED, applicationOrganization.getStatus());
        verify(applicationRepository, times(1)).save(applicationOrganization);
        verify(organizationRepository, times(1)).save(organization);
        verify(userEntityRepository, times(1)).save(user);
    }

    @Test
    void approveApplication_insufficientUserRights_returnRoleNotAllowedException() throws RoleNotAllowedException {
        Long applicationId = 1L;
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setStatus(Status.PENDING);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);

        UserEntity user = new UserEntity();
        user.setUserId(userId);


        UpdateApplicationOrganizationInteractionDto applicationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> {
                    applicationOrganizationService.approveApplication(organizationId, applicationDtoRequest);
                });

        assertEquals(Status.PENDING, applicationOrganization.getStatus());
        verify(applicationRepository, times(0)).save(applicationOrganization);
        verify(organizationRepository, times(0)).save(organization);
        verify(userEntityRepository, times(0)).save(user);
    }

    @Test
    void rejectApplication_rejectApplicationInOrganizationByOrganizer_setRejectApplication() throws RoleNotAllowedException {

        Long applicationId = 1L;
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        applicationOrganization.setStatus(Status.PENDING);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));

        applicationOrganization.setOrganization(organization);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateApplicationOrganizationInteractionDto applicationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);

        applicationOrganizationService.rejectApplication(organizationId, applicationDtoRequest);

        assertEquals(Status.REJECTED, applicationOrganization.getStatus());


        verify(applicationRepository, times(1)).save(applicationOrganization);
    }

    @Test
    void rejectApplication_userDidNotPassTheVerificationForTheOrganizer_returnRoleNotAllowedException() throws RoleNotAllowedException {

        Long applicationId = 1L;
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganization = new ApplicationOrganization();
        applicationOrganization.setApplicationOrganizationId(applicationId);
        applicationOrganization.setStatus(Status.PENDING);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(new HashSet<>(Collections.singletonList(applicationOrganization)));

        applicationOrganization.setOrganization(organization);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        UpdateApplicationOrganizationInteractionDto applicationDtoRequest = new UpdateApplicationOrganizationInteractionDto();
        applicationDtoRequest.setApplicationOrganizationId(applicationId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(controllerHelper.getApplicationOrganizationOrThrowException(applicationId)).thenReturn(applicationOrganization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> { applicationOrganizationService.rejectApplication(organizationId, applicationDtoRequest);});

        assertEquals(Status.PENDING, applicationOrganization.getStatus());


        verify(applicationRepository, times(0)).save(applicationOrganization);
    }

    @Test
    void getSportsmenApplicationsForOrganization_getApplicationInOrganizationWithStatusPendingAndRoleSportsmen_returnApplicationOrganizationResponseDto() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganizationOne = new ApplicationOrganization();
        applicationOrganizationOne.setStatus(Status.PENDING);
        applicationOrganizationOne.setRoleSport(RoleSport.SPORTSMAN);

        ApplicationOrganization applicationOrganizationTwo = new ApplicationOrganization();
        applicationOrganizationTwo.setStatus(Status.PENDING);
        applicationOrganizationTwo.setRoleSport(RoleSport.SPORTSMAN);


        Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
        applicationOrganizations.add(applicationOrganizationOne);
        applicationOrganizations.add(applicationOrganizationTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(applicationOrganizations);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        ApplicationOrganizationResponseDto dtoOne = Mockito.mock(ApplicationOrganizationResponseDto.class);
        ApplicationOrganizationResponseDto dtoTwo = Mockito.mock(ApplicationOrganizationResponseDto.class);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);
        when(applicationFactory.makeApplicationOrganizationDto(applicationOrganizationOne)).thenReturn(dtoOne);
        when(applicationFactory.makeApplicationOrganizationDto(applicationOrganizationTwo)).thenReturn(dtoTwo);

        List<ApplicationOrganizationResponseDto> result = applicationOrganizationService.getSportsmenApplicationsForOrganization(organizationId);

        assertEquals(2, result.size());

        verify(applicationFactory, times(1)).makeApplicationOrganizationDto(applicationOrganizationOne);
        verify(applicationFactory, times(1)).makeApplicationOrganizationDto(applicationOrganizationTwo);

    }

    @Test
    void getSportsmenApplicationsForOrganization_userDidNotPassTheVerificationForTheOrganizer_returnRoleNotAllowedException() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganizationOne = new ApplicationOrganization();
        applicationOrganizationOne.setStatus(Status.PENDING);
        applicationOrganizationOne.setRoleSport(RoleSport.SPORTSMAN);

        ApplicationOrganization applicationOrganizationTwo = new ApplicationOrganization();
        applicationOrganizationTwo.setStatus(Status.PENDING);
        applicationOrganizationTwo.setRoleSport(RoleSport.SPORTSMAN);


        Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
        applicationOrganizations.add(applicationOrganizationOne);
        applicationOrganizations.add(applicationOrganizationTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(applicationOrganizations);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> { applicationOrganizationService.getSportsmenApplicationsForOrganization(organizationId);});

        verify(applicationFactory, times(0)).makeApplicationOrganizationDto(applicationOrganizationOne);
        verify(applicationFactory, times(0)).makeApplicationOrganizationDto(applicationOrganizationTwo);

    }

    @Test
    void getCoachApplicationsForOrganization_getApplicationInOrganizationWithStatusPendingAndRoleCoach_returnApplicationOrganizationResponseDto() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganizationOne = new ApplicationOrganization();
        applicationOrganizationOne.setStatus(Status.PENDING);
        applicationOrganizationOne.setRoleSport(RoleSport.COACH);

        ApplicationOrganization applicationOrganizationTwo = new ApplicationOrganization();
        applicationOrganizationTwo.setStatus(Status.PENDING);
        applicationOrganizationTwo.setRoleSport(RoleSport.COACH);


        Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
        applicationOrganizations.add(applicationOrganizationOne);
        applicationOrganizations.add(applicationOrganizationTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(applicationOrganizations);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        ApplicationOrganizationResponseDto dtoOne = Mockito.mock(ApplicationOrganizationResponseDto.class);
        ApplicationOrganizationResponseDto dtoTwo = Mockito.mock(ApplicationOrganizationResponseDto.class);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(true);
        when(applicationFactory.makeApplicationOrganizationDto(applicationOrganizationOne)).thenReturn(dtoOne);
        when(applicationFactory.makeApplicationOrganizationDto(applicationOrganizationTwo)).thenReturn(dtoTwo);

        List<ApplicationOrganizationResponseDto> result = applicationOrganizationService.getCoachesApplicationsForOrganization(organizationId);

        assertEquals(2, result.size());

        verify(applicationFactory, times(1)).makeApplicationOrganizationDto(applicationOrganizationOne);
        verify(applicationFactory, times(1)).makeApplicationOrganizationDto(applicationOrganizationTwo);

    }

    @Test
    void getCoachApplicationsForOrganization_userDidNotPassTheVerificationForTheOrganizer_returnRoleNotAllowedException() throws RoleNotAllowedException {
        Long userId = 1L;
        Long organizationId = 1L;

        ApplicationOrganization applicationOrganizationOne = new ApplicationOrganization();
        applicationOrganizationOne.setStatus(Status.PENDING);
        applicationOrganizationOne.setRoleSport(RoleSport.COACH);

        ApplicationOrganization applicationOrganizationTwo = new ApplicationOrganization();
        applicationOrganizationTwo.setStatus(Status.PENDING);
        applicationOrganizationTwo.setRoleSport(RoleSport.COACH);


        Set<ApplicationOrganization> applicationOrganizations = new HashSet<>();
        applicationOrganizations.add(applicationOrganizationOne);
        applicationOrganizations.add(applicationOrganizationTwo);

        OrganizationEntity organization = new OrganizationEntity();
        organization.setOrganizationId(organizationId);
        organization.setApplicationOrganizationSet(applicationOrganizations);

        UserEntity user = new UserEntity();
        user.setUserId(userId);

        when(controllerHelper.getOrganizationOrThrowException(organizationId)).thenReturn(organization);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userRoleChecker.isOrganizer(userId, organization)).thenReturn(false);

        assertThrows(RoleNotAllowedException.class, () -> { applicationOrganizationService.getSportsmenApplicationsForOrganization(organizationId);});

        verify(applicationFactory, times(0)).makeApplicationOrganizationDto(applicationOrganizationOne);
        verify(applicationFactory, times(0)).makeApplicationOrganizationDto(applicationOrganizationTwo);

    }


}
