package zChampions.catalogue.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import zChampions.catalogue.controller.ControllerHelper;
import zChampions.catalogue.factories.*;
import zChampions.catalogue.requestDto.*;
import zChampions.catalogue.entity.*;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.exceptions.NotFoundException;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.createRequest.CreateAthleteProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateCoachProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateJudgeProfileRequestDto;
import zChampions.catalogue.requestDto.createRequest.CreateProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.BasicProfileEditRequest;
import zChampions.catalogue.requestDto.updateRequest.UpdateAthleteProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateCoachProfileRequestDto;
import zChampions.catalogue.requestDto.updateRequest.UpdateJudgeProfileRequestDto;
import zChampions.catalogue.responseDto.*;
import zChampions.catalogue.security.config.MyUserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    private final ValidationErrors validationErrors;
    private final UserRepository userEntityRepository;
    private final UserFactory userDtoFactory;
    private final ControllerHelper controllerHelper;
    private final ProfileCreateResponseDtoFactory sportProfileUserFactory;
    private final AthleteProfileRepository athleteProfileRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final JudgeProfileRepository judgeProfileRepository;
    private final BasicProfileResponseDtoFactory basicProfileResponseDtoFactory;
    private final AthleteProfileDtoResponseFactory athleteProfileDtoResponseFactory;
    private final CoachProfileUpdateResponseDtoFactory coachProfileUpdateResponseDtoFactory;
    private final JudgeProfileResponseDtoFactory judgeProfileResponseDtoFactory;
    private final AuthenticationService authenticationService;




    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", email)
        ));

        logger.info("User found: {}", user.getEmail());
        logger.info("Stored password: {}", user.getPassword());
        return MyUserDetails.buildUserDetails(user);
    }

    public List<UserResponseDto> findAllUsers() throws NotFoundException {

        List<UserEntity> users = userEntityRepository.findAll();
        if (users == null || users.isEmpty()) {
            logger.warn("No users were found.");
            return new ArrayList<>();
        } else {
            return users.stream()
                    .map(userDtoFactory::makeUserDto)
                    .collect(Collectors.toList());
        }
    }

    public ResponseEntity<BasicProfileResponseDto> basicEditProfile(Long userId, @Valid @RequestBody BasicProfileEditRequest basicProfileEdit, BindingResult bindingResult) throws BadRequestException{
        Long userWebsiteId = authenticationService.getCurrentUserId();

        logger.info("Create profile with parameters: firstName {}, lastName {}, patronymic {}, gender {}, dateOfBirth {}, height {}," +
                        "weight {}, country {}, region {}, city {}" , basicProfileEdit.getFirstName(), basicProfileEdit.getLastName(), basicProfileEdit.getPatronymic(),
                basicProfileEdit.getGender(),  basicProfileEdit.getDateOfBirth(), basicProfileEdit.getHeight(), basicProfileEdit.getWeight(), basicProfileEdit.getCountry(), basicProfileEdit.getRegion(),
                basicProfileEdit.getCity());

        if (!userId.equals(userWebsiteId)) {
            logger.error("updating the basic profile failed: the user: {} is not associated with this account: {}", userWebsiteId, userId);
            throw new BadRequestException("Unauthorized attempt to modify another user's profile.");
        }

        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while basic edit profile for user: {}", result);
            throw new BadRequestException(result);

        }

        UserEntity entity = controllerHelper.getUserOrThrowException(userWebsiteId);

        if (basicProfileEdit.getPassword() != null) {
            entity.setPassword(basicProfileEdit.getPassword());
        }

        if (basicProfileEdit.getFirstName() != null) {
            entity.setFirstName(basicProfileEdit.getFirstName());
        }
        if (basicProfileEdit.getLastName() != null) {
            entity.setLastName(basicProfileEdit.getLastName());
        }
        if (basicProfileEdit.getPatronymic() != null) {
            entity.setPatronymic(basicProfileEdit.getPatronymic());
        }
        if (basicProfileEdit.getPassword() != null) {
            entity.setPassword(basicProfileEdit.getPassword());
        }
        if (basicProfileEdit.getGender() != null) {
            entity.setGender(basicProfileEdit.getGender());
        }
        if (basicProfileEdit.getDateOfBirth() != null) {
            entity.setDateOfBirth(basicProfileEdit.getDateOfBirth());
        }
        if (basicProfileEdit.getHeight() != null) {
            entity.setHeight(basicProfileEdit.getHeight());
        }
        if (basicProfileEdit.getWeight() != null) {
            entity.setWeight(basicProfileEdit.getWeight());
        }
        if (basicProfileEdit.getCountry() != null) {
            entity.setCountry(basicProfileEdit.getCountry());
        }
        if (basicProfileEdit.getRegion() != null) {
            entity.setRegion(basicProfileEdit.getRegion());
        }
        if (basicProfileEdit.getCity() != null) {
            entity.setCity(basicProfileEdit.getCity());
        }

        userEntityRepository.save(entity);

        return ResponseEntity.ok(basicProfileResponseDtoFactory.makeBasicProfileResponseDto(entity));

    }

    public ResponseEntity<BasicProfileResponseDto> updateProfileAboutUser(Long userId, UpdateInformationEditProfileRequest informationEdit, BindingResult bindingResult) throws BadRequestException {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        if (!userId.equals(userWebsiteId)) {
            logger.error("profile about by user update failed: the user: {} is not associated with this account: {}", userWebsiteId, userId);
            throw new BadRequestException("Unauthorized attempt to modify another user's profile.");
        }

        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Verification errors occurred when adding or changing user information: {}", result);
            throw new BadRequestException(result);

        }
        UserEntity userEntity = controllerHelper.getUserOrThrowException(userWebsiteId);
        userEntity.setInformation(informationEdit.getInformation());
        userEntityRepository.saveAndFlush(userEntity);
        return ResponseEntity.ok(basicProfileResponseDtoFactory.makeBasicProfileResponseDto(userEntity));
    }



    public ProfileCreateResponseDto createProfile(Long userId, @Valid @RequestBody CreateProfileRequestDto profileRequestDto, BindingResult bindingResult) throws BadRequestException {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        if (!userId.equals(userWebsiteId)) {
            logger.error("profile creation failed: the user: {} is not associated with this account: {}", userWebsiteId, userId);
            throw new BadRequestException("Unauthorized attempt to modify another user's profile.");
        }


        if (bindingResult.hasErrors()) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while create profile for user: {}", result);
            throw new BadRequestException(result);
        }


        UserEntity userEntity = controllerHelper.getUserOrThrowException(userId);

        ProfileCreateResponseDto responseDto = new ProfileCreateResponseDto();

        if (profileRequestDto.getAthleteProfileDetails() != null) {
            AthleteProfileResponseDto athleteResponse = createAthleteProfile(userEntity, profileRequestDto.getAthleteProfileDetails());
            if (athleteResponse != null) {
                responseDto.setAthleteProfiles(new HashSet<>(Collections.singletonList(athleteResponse)));
            }
        }

        if (profileRequestDto.getCoachProfileDetails() != null) {
            CoachProfileUpdateResponseDto coachResponse = createCoachProfile(userEntity, profileRequestDto.getCoachProfileDetails());
            if (coachResponse != null) {
                responseDto.setCoachProfiles(new HashSet<>(Collections.singletonList(coachResponse)));
            }
        }

        if (profileRequestDto.getJudgeProfileDetails() != null) {
            JudgeProfileResponseDto judgeResponse = createJudgeProfile(userEntity, profileRequestDto.getJudgeProfileDetails());
            if(judgeResponse != null) {
                responseDto.setJudgeProfiles(new HashSet<>(Collections.singletonList(judgeResponse)));

            }
        }
        
        return responseDto;
    }

    private AthleteProfileResponseDto createAthleteProfile(UserEntity userEntity,
                                                           CreateAthleteProfileRequestDto athleteProfileRequestDto) {
        logger.info("Creating athlete profile for user: {}, roleSport: {}, typeOfSport: {}, experience: {}, rankAthlete: {}, disciplines: {},  ", userEntity.getUserId(), athleteProfileRequestDto.getRoleSport(),
                athleteProfileRequestDto.getTypeOfSport(), athleteProfileRequestDto.getExperience(), athleteProfileRequestDto.getRankAthlete(), athleteProfileRequestDto.getDisciplines());

        AthleteProfile athleteProfile = new AthleteProfile();
        athleteProfile.setUser(userEntity); // Используйте Set
        athleteProfile.setTypeOfSport(athleteProfileRequestDto.getTypeOfSport());
        athleteProfile.setExperience(athleteProfileRequestDto.getExperience()); // Теперь берется из athleteProfileRequestDto
        athleteProfile.setRankAthlete(athleteProfileRequestDto.getRankAthlete());
        athleteProfile.setDisciplines(athleteProfileRequestDto.getDisciplines());
        athleteProfile.setRoleSport(athleteProfileRequestDto.getRoleSport());

        athleteProfileRepository.save(athleteProfile);
        userEntity.getAthleteProfiles().add(athleteProfile);
        userEntityRepository.save(userEntity);

        logger.info("Successfully created athlete profile for user ID {}", userEntity.getUserId());
        return athleteProfileDtoResponseFactory.makeAthleteProfileResponseDto(athleteProfile);
    }

    private CoachProfileUpdateResponseDto createCoachProfile(UserEntity userEntity,
                                                             CreateCoachProfileRequestDto coachProfileRequestDto) {
        logger.info("Creating athlete profile for user: {}, roleSport: {}, typeOfSport: {}, category: {}, specialization: {}, hourlyRate: {},  ", userEntity.getUserId(), coachProfileRequestDto.getRoleSport(),
                coachProfileRequestDto.getTypeOfSport(), coachProfileRequestDto.getCategory(), coachProfileRequestDto.getSpecialization(), coachProfileRequestDto.getHourlyRate());

        CoachProfile coachProfile = new CoachProfile();
        coachProfile.setUser(userEntity);
        coachProfile.setTypeOfSport(coachProfileRequestDto.getTypeOfSport());
        coachProfile.setCategory(coachProfileRequestDto.getCategory());
        coachProfile.setSpecialization(coachProfileRequestDto.getSpecialization());
        coachProfile.setHourlyRate(coachProfileRequestDto.getHourlyRate());
        coachProfile.setTypeOfSport(coachProfileRequestDto.getTypeOfSport());
        coachProfile.setRoleSport(coachProfileRequestDto.getRoleSport());
        coachProfileRepository.save(coachProfile);

        userEntity.getCoachProfiles().add(coachProfile);
        userEntityRepository.save(userEntity);

        logger.info("Successfully created coach profile for user ID {}", userEntity.getUserId());
        return coachProfileUpdateResponseDtoFactory.makeCoachProfileUpdateResponseDto(coachProfile);
    }

    private JudgeProfileResponseDto createJudgeProfile(UserEntity userEntity,
                                                       CreateJudgeProfileRequestDto judgeProfileRequestDto) {

        logger.info("Creating athlete profile for user: {}, roleSport: {}, typeOfSport: {}, qualification: {}", userEntity.getUserId(), judgeProfileRequestDto.getRoleSport(),
                judgeProfileRequestDto.getTypeOfSport(), judgeProfileRequestDto.getQualification());

        JudgeProfile judgeProfile = new JudgeProfile();
        judgeProfile.setUser(userEntity);
        judgeProfile.setTypeOfSport(judgeProfileRequestDto.getTypeOfSport());
        judgeProfile.setQualification(judgeProfileRequestDto.getQualification());
        judgeProfile.setRoleSport(judgeProfileRequestDto.getRoleSport());

        judgeProfileRepository.save(judgeProfile);

        userEntity.getJudgeProfiles().add(judgeProfile);
        userEntityRepository.save(userEntity);

        logger.info("Successfully created judge profile for user ID {}", userEntity.getUserId());
        return judgeProfileResponseDtoFactory.makeJudgeProfileResponseDto(judgeProfile);
    }

    public AthleteProfileResponseDto updateAthleteProfile(Long userId, Long athleteProfileId, UpdateAthleteProfileRequestDto updateRequest) throws BadRequestException {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        logger.info("Updating athlete profile with parameters:  kindOfSport {}, experience {}, rankAthlete {}, disciplines {}", updateRequest.getTypeOfSport(), updateRequest.getExperience(), updateRequest.getRankAthlete(), updateRequest.getDisciplines());

        AthleteProfile athleteProfile = controllerHelper.getAthleteProfileOrThrowException(athleteProfileId);

        boolean userExists = athleteProfileRepository.findAthleteProfileByUser(userWebsiteId, athleteProfileId);

        if(!userExists) {
            logger.warn("User with ID {} not registered for this athlete profile: {}", userWebsiteId, athleteProfileId);
            throw new SecurityException("User does not have access to this profile");
        }

        if (updateRequest.getRankAthlete() != null) {
            athleteProfile.setRankAthlete(updateRequest.getRankAthlete());
        }

        if (updateRequest.getDisciplines() != null) {
            athleteProfile.setDisciplines(updateRequest.getDisciplines());
        }

        if (updateRequest.getExperience() != null) {
            athleteProfile.setExperience(updateRequest.getExperience());
        }
        if(updateRequest.getTypeOfSport() != null) {
            athleteProfile.setTypeOfSport(updateRequest.getTypeOfSport());
        }

        logger.info("Athlete profile with ID {} updated successfully", athleteProfileId);
         athleteProfileRepository.save(athleteProfile);
         return athleteProfileDtoResponseFactory.makeAthleteProfileResponseDto(athleteProfile);
    }

    public CoachProfileUpdateResponseDto updateCoachProfile(Long userId, Long coachProfileId, UpdateCoachProfileRequestDto updateRequest) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        CoachProfile coachProfile = controllerHelper.getCoachProfileOrThrowException(coachProfileId);

        boolean userExists = coachProfileRepository.findCoachProfileByUser(userWebsiteId, coachProfileId);


        if (!userExists) {
            logger.warn("User with ID {} not registered for this coach profile: {}", userWebsiteId, coachProfileId);
            throw new SecurityException("User does not have access to this profile");
        }

        // Обновление полей профиля
        if (updateRequest.getCategory() != null) {
            coachProfile.setCategory(updateRequest.getCategory());
        }

        if (updateRequest.getSpecialization() != null) {
            coachProfile.setSpecialization(updateRequest.getSpecialization());
        }

        if (updateRequest.getHourlyRate() != null) {
            coachProfile.setHourlyRate(updateRequest.getHourlyRate());
        }
        if(updateRequest.getTypeOfSport() != null) {
            coachProfile.setTypeOfSport(updateRequest.getTypeOfSport());
        }

        coachProfileRepository.save(coachProfile);
        return coachProfileUpdateResponseDtoFactory.makeCoachProfileUpdateResponseDto(coachProfile);
    }

    public JudgeProfileResponseDto updateJudgeProfile(Long userId, Long judgeProfileId, UpdateJudgeProfileRequestDto updateRequest) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        JudgeProfile judgeProfile = controllerHelper.getJudgeProfileOrThrowException(judgeProfileId);

        boolean userExists = judgeProfileRepository.findJudgeProfileByUser(userWebsiteId, judgeProfileId);


        if (!userExists) {
            logger.warn("User with ID {} not registered for this judge profile: {}", userWebsiteId, judgeProfileId);
            throw new SecurityException("User does not have access to this profile");
        }

        if (updateRequest.getQualification() != null) {
            judgeProfile.setQualification(updateRequest.getQualification());
        }
        if(updateRequest.getTypeOfSport() != null) {
            judgeProfile.setTypeOfSport(updateRequest.getTypeOfSport());
        }

        judgeProfileRepository.save(judgeProfile);
        return judgeProfileResponseDtoFactory.makeJudgeProfileResponseDto(judgeProfile);
    }

    public void deleteAthleteProfile(Long userId, Long athleteProfileId) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        logger.info("delete athlete profile: {}, by user: {}", userWebsiteId, athleteProfileId);

        boolean userExists = athleteProfileRepository.findAthleteProfileByUser(userWebsiteId, athleteProfileId);

        if(!userExists) {
            logger.warn("delete athlete profile method: User with ID {} not registered for this athlete profile: {}", userWebsiteId, athleteProfileId);
            throw new SecurityException("User does not have access to this profile");
        }


        AthleteProfile athleteProfile = controllerHelper.getAthleteProfileOrThrowException(athleteProfileId);

        athleteProfileRepository.deleteById(athleteProfileId);

    }

    public void deleteCoachProfile(Long userId, Long coachProfileId) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        logger.info("delete coach profile: {}, by user: {}", userWebsiteId, coachProfileId);

        boolean userExists = coachProfileRepository.findCoachProfileByUser(userWebsiteId, coachProfileId);

        if(!userExists) {
            logger.warn("delete coach profile method: User with ID {} not registered for this coach profile: {}", userWebsiteId, coachProfileId);
            throw new SecurityException("User does not have access to this profile");
        }

        CoachProfile coachProfile = controllerHelper.getCoachProfileOrThrowException(coachProfileId);


        coachProfileRepository.deleteById(coachProfileId);
    }

    public void deleteJudgeProfile(Long userId, Long judgeProfileId) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        logger.info("delete judge profile: {}, by user: {}", userWebsiteId, judgeProfileId);

        boolean userExists = judgeProfileRepository.findJudgeProfileByUser(judgeProfileId, userWebsiteId);

        if(!userExists) {
            logger.warn("delete judge profile method: User with ID {} not registered for this judge profile: {}", userWebsiteId, judgeProfileId);
            throw new SecurityException("User does not have access to this profile");
        }


        JudgeProfile judgeProfile = controllerHelper.getJudgeProfileOrThrowException(judgeProfileId);

        judgeProfileRepository.deleteById(judgeProfileId);
    }

    public void deleteUser(Long id) {
        Long userWebsiteId = authenticationService.getCurrentUserId();

        controllerHelper.getUserOrThrowException(userWebsiteId);
        userEntityRepository.deleteById(userWebsiteId);
        ResponseEntity.noContent()
                .build();
    }
}
