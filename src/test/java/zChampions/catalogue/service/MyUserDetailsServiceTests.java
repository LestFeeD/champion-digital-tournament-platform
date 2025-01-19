package zChampions.catalogue.service;

import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.entity.AthleteProfile;
import zChampions.catalogue.entity.CoachProfile;
import zChampions.catalogue.entity.JudgeProfile;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.enumsEntities.*;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.factories.*;
import zChampions.catalogue.repository.AthleteProfileRepository;
import zChampions.catalogue.repository.CoachProfileRepository;
import zChampions.catalogue.repository.JudgeProfileRepository;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.requestDto.createRequest.CreateAthleteProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateCoachProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateJudgeProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.BasicProfileEditRequest;
import zChampions.catalogue.requestDto.updateRequest.UpdateAthleteProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateCoachProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateJudgeProfileRequestDto;
import zChampions.catalogue.responseDto.AthleteProfileResponseDto;
import zChampions.catalogue.responseDto.CoachProfileUpdateResponseDto;
import zChampions.catalogue.responseDto.JudgeProfileResponseDto;
import zChampions.catalogue.responseDto.ProfileCreateResponseDto;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static zChampions.catalogue.enumsEntities.Disciplines.FOOTBALL;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTests {

    @Mock
    private  ValidationErrors validationErrors;
    @Mock
    private  UserRepository userEntityRepository;
    @Mock
    private  UserFactory userDtoFactory;
    @Mock
    private  ControllerHelper controllerHelper;
    @Mock
    private  AthleteProfileRepository athleteProfileRepository;
    @Mock
    private  CoachProfileRepository coachProfileRepository;
    @Mock
    private  JudgeProfileRepository judgeProfileRepository;
    @Mock
    private  BasicProfileResponseDtoFactory basicProfileResponseDtoFactory;
    @Mock
    private  AthleteProfileDtoResponseFactory athleteProfileDtoResponseFactory;
    @Mock
    private  CoachProfileUpdateResponseDtoFactory coachProfileUpdateResponseDtoFactory;
    @Mock
    private  JudgeProfileResponseDtoFactory judgeProfileResponseDtoFactory;
    @Mock
    private BindingResult bindingResult;

    @Mock
    private  AuthenticationService authenticationService;

    @InjectMocks
    private  MyUserDetailsService myUserDetailsService;

    @Test
    void findAllUsers_findUser_returnListUsersDto(){

        List<UserEntity> userList = new ArrayList<>();
        userList.add(new UserEntity());
        userList.add(new UserEntity());


        when(userEntityRepository.findAll()).thenReturn(userList);

        myUserDetailsService.findAllUsers();

        verify(userEntityRepository, times(1)).findAll();
        verify(userDtoFactory, times(2)).makeUserDto(any(UserEntity.class));
    }

    @Test
    void findAllUsers_findUser_returnEmptyList(){

        List<UserEntity> userList = new ArrayList<>();

        when(userEntityRepository.findAll()).thenReturn(userList);

        myUserDetailsService.findAllUsers();

        verify(userEntityRepository, times(1)).findAll();
        verify(userDtoFactory,never()).makeUserDto(any(UserEntity.class));
    }

    @Test
    void basicEditProfile_editBasicProfileUser_returnBasicProfileResponseDto() throws BadRequestException {
        Long userId = 1L;
        String newTestName = "newTestName";

        UserEntity user = new UserEntity();
        user.setFirstName("testName");

        BasicProfileEditRequest basicProfileEditRequest = new BasicProfileEditRequest();
        basicProfileEditRequest.setFirstName(newTestName);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        myUserDetailsService.basicEditProfile(userId, basicProfileEditRequest, bindingResult);

        assertEquals(newTestName, user.getFirstName());
        verify(userEntityRepository, times(1)).save(user);
        verify(basicProfileResponseDtoFactory, times(1)).makeBasicProfileResponseDto(user);

    }

    @Test
    void basicEditProfile_validationError_returnBadRequestException() throws BadRequestException {
        Long userId = 1L;

        UserEntity user = new UserEntity();

        BasicProfileEditRequest basicProfileEditRequest = new BasicProfileEditRequest();

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> myUserDetailsService.basicEditProfile(userId, basicProfileEditRequest, bindingResult));

        verify(userEntityRepository,never()).save(user);
        verify(basicProfileResponseDtoFactory, never()).makeBasicProfileResponseDto(user);
    }

    @Test
    void createProfile_createAthleteProfileDetails_returnProfileCreateResponseDto() throws BadRequestException {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateAthleteProfileRequestDto athleteProfileRequestDto = new CreateAthleteProfileRequestDto();
        athleteProfileRequestDto.setRoleSport(RoleSport.SPORTSMAN);
        athleteProfileRequestDto.setRankAthlete(RankAthlete.MASTER_SPORTS);
        athleteProfileRequestDto.setDisciplines(Collections.singletonList(FOOTBALL));

        CreateProfileRequestDto profileRequestDto = new CreateProfileRequestDto();
        profileRequestDto.setAthleteProfileDetails(athleteProfileRequestDto);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        AthleteProfile athleteProfile = new AthleteProfile();
        athleteProfile.setTypeOfSport(athleteProfileRequestDto.getTypeOfSport());
        athleteProfile.setExperience(athleteProfileRequestDto.getExperience());
        athleteProfile.setRankAthlete(athleteProfileRequestDto.getRankAthlete());
        athleteProfile.setDisciplines(athleteProfileRequestDto.getDisciplines());
        athleteProfile.setRoleSport(athleteProfileRequestDto.getRoleSport());
        user.setAthleteProfiles(new HashSet<>(Collections.singletonList(athleteProfile)));
        athleteProfile.setUser(user);

        when(athleteProfileRepository.save(any(AthleteProfile.class))).thenReturn(athleteProfile);
        AthleteProfileResponseDto athleteProfileResponseDto = AthleteProfileResponseDto.builder()
                .typeOfSport(athleteProfileRequestDto.getTypeOfSport())
                .experience(athleteProfileRequestDto.getExperience())
                .rankAthlete(athleteProfileRequestDto.getRankAthlete())
                .disciplines(athleteProfileRequestDto.getDisciplines())
                .roleSport(athleteProfileRequestDto.getRoleSport())
                .build();
        

        myUserDetailsService.createProfile(userId, profileRequestDto, bindingResult);

        assertEquals(RoleSport.SPORTSMAN, athleteProfileResponseDto.getRoleSport());
    }

    @Test
    void createProfile_createCoachProfileDetails_returnProfileCreateResponseDto() throws BadRequestException {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateCoachProfileRequestDto coachProfileRequestDto = new CreateCoachProfileRequestDto();
        coachProfileRequestDto.setRoleSport(RoleSport.COACH);
        coachProfileRequestDto.setCategory(Category.MEDALIST_COACH);
        coachProfileRequestDto.setTypeOfSport(KindOfSport.FOOTBALL);

        CreateProfileRequestDto profileRequestDto = new CreateProfileRequestDto();
        profileRequestDto.setCoachProfileDetails(coachProfileRequestDto);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setTypeOfSport(coachProfileRequestDto.getTypeOfSport());
        coachProfile.setRoleSport(coachProfileRequestDto.getRoleSport());
        coachProfile.setCategory(coachProfileRequestDto.getCategory());
        user.setCoachProfiles(new HashSet<>(Collections.singletonList(coachProfile)));
        coachProfile.setUser(user);

        when(coachProfileRepository.save(any(CoachProfile.class))).thenReturn(coachProfile);
        CoachProfileUpdateResponseDto coachProfileUpdateResponseDto = CoachProfileUpdateResponseDto.builder()
                .typeOfSport(coachProfileRequestDto.getTypeOfSport())
                .category(coachProfileRequestDto.getCategory())
                .roleSport(coachProfileRequestDto.getRoleSport())
                .build();

        myUserDetailsService.createProfile(userId, profileRequestDto, bindingResult);

        assertEquals(RoleSport.COACH, coachProfileUpdateResponseDto.getRoleSport());
    }

    @Test
    void createProfile_createJudgeProfileDetails_returnProfileCreateResponseDto() throws BadRequestException {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateJudgeProfileRequestDto judgeProfileRequestDto = new CreateJudgeProfileRequestDto();
        judgeProfileRequestDto.setRoleSport(RoleSport.JUDGE);
        judgeProfileRequestDto.setQualification(Qualification.SPORTS_REFEREE_OF_THE_ALL_RUSSIAN_CATEGORY);
        judgeProfileRequestDto.setTypeOfSport(KindOfSport.FOOTBALL);

        CreateProfileRequestDto profileRequestDto = new CreateProfileRequestDto();
        profileRequestDto.setJudgeProfileDetails(judgeProfileRequestDto);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        JudgeProfile judgeProfile = new JudgeProfile();
        judgeProfile.setTypeOfSport(judgeProfileRequestDto.getTypeOfSport());
        judgeProfile.setRoleSport(judgeProfileRequestDto.getRoleSport());
        judgeProfile.setQualification(judgeProfileRequestDto.getQualification());
        user.setJudgeProfiles(new HashSet<>(Collections.singletonList(judgeProfile)));
        judgeProfile.setUser(user);

        when(judgeProfileRepository.save(any(JudgeProfile.class))).thenReturn(judgeProfile);
        JudgeProfileResponseDto judgeProfileResponseDto = JudgeProfileResponseDto.builder()
                .typeOfSport(judgeProfileRequestDto.getTypeOfSport())
                .qualification(judgeProfileRequestDto.getQualification())
                .roleSport(judgeProfileRequestDto.getRoleSport())
                .build();

        myUserDetailsService.createProfile(userId, profileRequestDto, bindingResult);

        assertEquals(RoleSport.JUDGE, judgeProfileResponseDto.getRoleSport());
    }

    @Test
    void createProfile_createJudgeProfileDetailsAndCoachProfileDetails_returnProfileCreateResponseDto() throws BadRequestException {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        CreateCoachProfileRequestDto coachProfileRequestDto = new CreateCoachProfileRequestDto();
        coachProfileRequestDto.setRoleSport(RoleSport.COACH);
        coachProfileRequestDto.setCategory(Category.MEDALIST_COACH);
        coachProfileRequestDto.setTypeOfSport(KindOfSport.FOOTBALL);

        CreateJudgeProfileRequestDto judgeProfileRequestDto = new CreateJudgeProfileRequestDto();
        judgeProfileRequestDto.setRoleSport(RoleSport.JUDGE);
        judgeProfileRequestDto.setQualification(Qualification.SPORTS_REFEREE_OF_THE_ALL_RUSSIAN_CATEGORY);
        judgeProfileRequestDto.setTypeOfSport(KindOfSport.FOOTBALL);

        CreateProfileRequestDto profileRequestDto = new CreateProfileRequestDto();
        profileRequestDto.setJudgeProfileDetails(judgeProfileRequestDto);
        profileRequestDto.setCoachProfileDetails(coachProfileRequestDto);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setTypeOfSport(coachProfileRequestDto.getTypeOfSport());
        coachProfile.setRoleSport(coachProfileRequestDto.getRoleSport());
        coachProfile.setCategory(coachProfileRequestDto.getCategory());
        user.setCoachProfiles(new HashSet<>(Collections.singletonList(coachProfile)));
        coachProfile.setUser(user);

        JudgeProfile judgeProfile = new JudgeProfile();
        judgeProfile.setTypeOfSport(judgeProfileRequestDto.getTypeOfSport());
        judgeProfile.setRoleSport(judgeProfileRequestDto.getRoleSport());
        judgeProfile.setQualification(judgeProfileRequestDto.getQualification());
        user.setJudgeProfiles(new HashSet<>(Collections.singletonList(judgeProfile)));
        judgeProfile.setUser(user);


        when(coachProfileRepository.save(any(CoachProfile.class))).thenReturn(coachProfile);
        when(judgeProfileRepository.save(any(JudgeProfile.class))).thenReturn(judgeProfile);

        CoachProfileUpdateResponseDto coachProfileUpdateResponseDto = CoachProfileUpdateResponseDto.builder()
                .typeOfSport(coachProfileRequestDto.getTypeOfSport())
                .category(coachProfileRequestDto.getCategory())
                .roleSport(coachProfileRequestDto.getRoleSport())
                .build();

        JudgeProfileResponseDto judgeProfileResponseDto = JudgeProfileResponseDto.builder()
                .typeOfSport(judgeProfileRequestDto.getTypeOfSport())
                .qualification(judgeProfileRequestDto.getQualification())
                .roleSport(judgeProfileRequestDto.getRoleSport())
                .build();

        myUserDetailsService.createProfile(userId, profileRequestDto, bindingResult);

        assertEquals(RoleSport.COACH, coachProfileUpdateResponseDto.getRoleSport());
        assertEquals(RoleSport.JUDGE, judgeProfileResponseDto.getRoleSport());

    }

    @Test
    void createProfile_validationError_returnBadRequestException() throws BadRequestException {
        Long userId = 1L;

        CreateProfileRequestDto profileRequestDto = new CreateProfileRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> myUserDetailsService.createProfile(userId, profileRequestDto, bindingResult));

    }

    @Test
    void updateAthleteProfile_updateAthleteProfileForTheUser_returnAthleteProfileResponseDto() throws BadRequestException {

        UpdateAthleteProfileRequestDto updateAthleteProfileRequestDto = new UpdateAthleteProfileRequestDto();
        updateAthleteProfileRequestDto.setTypeOfSport(KindOfSport.HOCKEY);

        UserEntity user = new UserEntity();
        user.setUserId(1L);

        AthleteProfile athleteProfile = new AthleteProfile();
        athleteProfile.setAthleteProfileId(1L);
        athleteProfile.setUser(user);
        athleteProfile.setTypeOfSport(KindOfSport.BASKETBALL);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getAthleteProfileOrThrowException(1L)).thenReturn(athleteProfile);
        when(athleteProfileRepository.findAthleteProfileByUser(1L, 1L)).thenReturn(true);
        when(athleteProfileRepository.save(athleteProfile)).thenReturn(athleteProfile);

        myUserDetailsService.updateAthleteProfile(1L, 1L, updateAthleteProfileRequestDto);

        assertEquals(KindOfSport.HOCKEY, athleteProfile.getTypeOfSport());

        verify(athleteProfileDtoResponseFactory, times(1)).makeAthleteProfileResponseDto(athleteProfile);
    }

    @Test
    void updateAthleteProfile_notFoundTheAthleteProfileForTheUser_returnNotFoundException() throws NotFoundException {

        UpdateAthleteProfileRequestDto updateAthleteProfileRequestDto = new UpdateAthleteProfileRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getAthleteProfileOrThrowException(1L)).thenThrow(new NotFoundException("Athlete profile not found"));

        assertThrows(NotFoundException.class, () ->  myUserDetailsService.updateAthleteProfile(1L, 1L, updateAthleteProfileRequestDto));

        verify(athleteProfileDtoResponseFactory, never()).makeAthleteProfileResponseDto(any());
        verify(athleteProfileRepository, never()).save(any());
    }

    @Test
    void updateAthleteProfile_theProfileIsNotUser_returnSecurityException() throws SecurityException {

        UpdateAthleteProfileRequestDto updateAthleteProfileRequestDto = new UpdateAthleteProfileRequestDto();

        AthleteProfile athleteProfile = new AthleteProfile();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getAthleteProfileOrThrowException(1L)).thenReturn(athleteProfile);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.updateAthleteProfile(1L, 1L, updateAthleteProfileRequestDto));

        verify(athleteProfileDtoResponseFactory, never()).makeAthleteProfileResponseDto(athleteProfile);
        verify(athleteProfileRepository, never()).save(athleteProfile);
    }

    @Test
    void updateCoachProfile_updateCoachProfileForTheUser_returnCoachProfileUpdateResponseDto() throws BadRequestException {

        UpdateCoachProfileRequestDto updateCoachProfileRequestDto = new UpdateCoachProfileRequestDto();
        updateCoachProfileRequestDto.setTypeOfSport(KindOfSport.HOCKEY);

        UserEntity user = new UserEntity();
        user.setUserId(1L);

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setCoachProfileId(1L);
        coachProfile.setUser(user);
        coachProfile.setTypeOfSport(KindOfSport.BASKETBALL);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getCoachProfileOrThrowException(1L)).thenReturn(coachProfile);
        when(coachProfileRepository.findCoachProfileByUser(1L, 1L)).thenReturn(true);
        when(coachProfileRepository.save(coachProfile)).thenReturn(coachProfile);

        myUserDetailsService.updateCoachProfile(1L, 1L, updateCoachProfileRequestDto);

        assertEquals(KindOfSport.HOCKEY, coachProfile.getTypeOfSport());

        verify(coachProfileUpdateResponseDtoFactory, times(1)).makeCoachProfileUpdateResponseDto(coachProfile);
    }

    @Test
    void  updateCoachProfile_notFoundTheCoachProfileForTheUser_returnNotFoundException() throws NotFoundException {

        UpdateCoachProfileRequestDto updateCoachProfileRequestDto = new UpdateCoachProfileRequestDto();


        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getCoachProfileOrThrowException(1L)).thenThrow(new NotFoundException("Coach profile not found"));

        assertThrows(NotFoundException.class, () ->  myUserDetailsService.updateCoachProfile(1L, 1L, updateCoachProfileRequestDto));

        verify(coachProfileUpdateResponseDtoFactory, never()).makeCoachProfileUpdateResponseDto(any());
        verify(coachProfileRepository, never()).save(any());
    }

    @Test
    void updateCoachProfile_theProfileIsNotUser_returnSecurityException() throws SecurityException {

        UpdateCoachProfileRequestDto updateCoachProfileRequestDto = new UpdateCoachProfileRequestDto();

        CoachProfile coachProfile = new CoachProfile();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getCoachProfileOrThrowException(1L)).thenReturn(coachProfile);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.updateCoachProfile(1L, 1L, updateCoachProfileRequestDto));

        verify(coachProfileUpdateResponseDtoFactory, never()).makeCoachProfileUpdateResponseDto(coachProfile);
        verify(coachProfileRepository, never()).save(coachProfile);
    }

    @Test
    void updateJudgeProfile_updateJudgeProfileForTheUser_returnCoachProfileUpdateResponseDto() throws BadRequestException {

        UpdateJudgeProfileRequestDto updateJudgeProfileRequestDto = new UpdateJudgeProfileRequestDto();
        updateJudgeProfileRequestDto.setTypeOfSport(KindOfSport.HOCKEY);

        UserEntity user = new UserEntity();
        user.setUserId(1L);

        JudgeProfile judgeProfile = new JudgeProfile();
        judgeProfile.setJudgeProfileId(1L);
        judgeProfile.setUser(user);
        judgeProfile.setTypeOfSport(KindOfSport.BASKETBALL);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getJudgeProfileOrThrowException(1L)).thenReturn(judgeProfile);
        when(judgeProfileRepository.findJudgeProfileByUser(1L, 1L)).thenReturn(true);
        when(judgeProfileRepository.save(judgeProfile)).thenReturn(judgeProfile);

        myUserDetailsService.updateJudgeProfile(1L, 1L, updateJudgeProfileRequestDto);

        assertEquals(KindOfSport.HOCKEY, judgeProfile.getTypeOfSport());

        verify(judgeProfileResponseDtoFactory, times(1)).makeJudgeProfileResponseDto(judgeProfile);
    }

    @Test
    void  updateJudgeProfile_notFoundTheJudgeProfileForTheUser_returnNotFoundException() throws NotFoundException {

        UpdateJudgeProfileRequestDto updateJudgeProfileRequestDto = new UpdateJudgeProfileRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getJudgeProfileOrThrowException(1L)).thenThrow(new NotFoundException("Coach profile not found"));

        assertThrows(NotFoundException.class, () ->  myUserDetailsService.updateJudgeProfile(1L, 1L, updateJudgeProfileRequestDto));

        verify(judgeProfileResponseDtoFactory, never()).makeJudgeProfileResponseDto(any());
        verify(judgeProfileRepository, never()).save(any());
    }

    @Test
    void updateJudgeProfile_theProfileIsNotUser_returnSecurityException() throws SecurityException {

        UpdateJudgeProfileRequestDto updateJudgeProfileRequestDto = new UpdateJudgeProfileRequestDto();

        JudgeProfile judgeProfile = new JudgeProfile();

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(controllerHelper.getJudgeProfileOrThrowException(1L)).thenReturn(judgeProfile);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.updateJudgeProfile(1L, 1L, updateJudgeProfileRequestDto));

        verify(judgeProfileResponseDtoFactory, never()).makeJudgeProfileResponseDto(judgeProfile);
        verify(judgeProfileRepository, never()).save(judgeProfile);
    }

    @Test
    void deleteAthleteProfile_deleteAthleteProfileByUser() {
        Long userId = 1L;
        Long athleteProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(controllerHelper.getAthleteProfileOrThrowException(1L)).thenReturn(new AthleteProfile());
        when(athleteProfileRepository.findAthleteProfileByUser(userId, athleteProfileId)).thenReturn(true);

        myUserDetailsService.deleteAthleteProfile(userId, athleteProfileId);

        verify(athleteProfileRepository, times(1)).deleteById(athleteProfileId);
    }

    @Test
    void deleteAthleteProfile_userNotAssociatedWithProfile_returnSecurityException () {
        Long userId = 1L;
        Long athleteProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(athleteProfileRepository.findAthleteProfileByUser(userId, athleteProfileId)).thenReturn(false);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.deleteAthleteProfile(userId, athleteProfileId));

        verify(athleteProfileRepository, never()).deleteById(athleteProfileId);
    }

    @Test
    void deleteCoachProfile_deleteCoachProfileByUser() {
        Long userId = 1L;
        Long coachProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(controllerHelper.getCoachProfileOrThrowException(1L)).thenReturn(new CoachProfile());
        when(coachProfileRepository.findCoachProfileByUser(userId, coachProfileId)).thenReturn(true);

        myUserDetailsService.deleteCoachProfile(userId, coachProfileId);

        verify(coachProfileRepository, times(1)).deleteById(coachProfileId);
    }

    @Test
    void deleteCoachProfile_userNotAssociatedWithProfile_returnSecurityException () {
        Long userId = 1L;
        Long coachProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(coachProfileRepository.findCoachProfileByUser(userId, coachProfileId)).thenReturn(false);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.deleteCoachProfile(userId, coachProfileId));

        verify(coachProfileRepository, never()).deleteById(coachProfileId);
    }

    @Test
    void deleteJudgeProfile_deleteJudgeProfileByUser() {
        Long userId = 1L;
        Long judgeProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(judgeProfileRepository.findJudgeProfileByUser(judgeProfileId, userId)).thenReturn(true);
        when(controllerHelper.getJudgeProfileOrThrowException(judgeProfileId)).thenReturn(new JudgeProfile());

        myUserDetailsService.deleteJudgeProfile(judgeProfileId, userId);

        verify(judgeProfileRepository, times(1)).findJudgeProfileByUser(judgeProfileId, userId);
        verify(judgeProfileRepository, times(1)).deleteById(judgeProfileId);
    }

    @Test
    void deleteJudgeProfile_userNotAssociatedWithProfile_returnSecurityException () {
        Long userId = 1L;
        Long judgeProfileId = 1L;

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(judgeProfileRepository.findJudgeProfileByUser(judgeProfileId, userId)).thenReturn(false);

        assertThrows(SecurityException.class, () ->  myUserDetailsService.deleteJudgeProfile(userId, judgeProfileId));

        verify(judgeProfileRepository, never()).deleteById(judgeProfileId);
    }
    
    

    @Test
    void deleteUser_deleteUserById() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);

        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(controllerHelper.getUserOrThrowException(userId)).thenReturn(user);

        myUserDetailsService.deleteUser(userId);
    }






 }
