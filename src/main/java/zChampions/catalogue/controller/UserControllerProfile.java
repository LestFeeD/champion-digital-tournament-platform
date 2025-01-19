package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.requestDto.updateRequest.BasicProfileEditRequest;
import zChampions.catalogue.requestDto.createRequest.CreateProfileRequestDto;
import zChampions.catalogue.requestDto.UpdateInformationEditProfileRequest;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.requestDto.updateRequest.UpdateProfileRequestDto;
import zChampions.catalogue.responseDto.*;
import zChampions.catalogue.service.MyUserDetailsService;

import java.util.List;
import java.util.Set;

@Tag(name = "class working with user profile")
@RestController
@RequiredArgsConstructor
@Transactional
public class UserControllerProfile {

    private final MyUserDetailsService userEditProfileService;

    public static final String BASIC_EDIT_PROFILE = "/edit-profile/{user_id}/basic";
    public static final String EDIT_PROFILE_INFORMATION_ABOUT_USER = "/edit-profile/{user_id}/about";
    public static final String EDIT_PROFILE_BASIC_SPORT = "/edit-profile/{user_id}/edit/sport";
    public static final String EDIT_PROFILE_BASIC_SPORT_ADDITIONALLY = "/edit-profile/{user_id}/sport/additionally";
    public static final String All_USERS = "/all-user";
    public static final String DELETE_USER = "/delete-user/{user_id}";
    public static final String DELETE_ATHLETE_PROFILE = "/delete-profile/{user_id}/delete/athlete/{athlete_profile_id}";
    public static final String DELETE_COACH_PROFILE = "/delete-profile/{user_id}/delete/coach/{coach_profile_id}";
    public static final String DELETE_JUDGE_PROFILE = "/delete-profile/{user_id}/delete/judge/{judge_profile_id}";

    private static final Logger log =  LoggerFactory.getLogger(UserControllerProfile.class);

    @Operation(
            summary = "Find all users in the system.",
            description = "The data is retrieved from the repository and returned as a DTO response."
    )
    @GetMapping(All_USERS)
    public List<UserResponseDto> getEvents() {
        return userEditProfileService.findAllUsers();
    }

    @Operation(
            summary = "Editing basic user information.",
            description = "Gets the ID of the user for whom the profile will be changed, along with the parameters in the DTO that need to be changed, and sends it to the service for processing."
    )
    @PatchMapping(BASIC_EDIT_PROFILE)
    public ResponseEntity<BasicProfileResponseDto> basicProfileEdit(@PathVariable("user_id") Long userId,
                                                                    @Valid @RequestBody BasicProfileEditRequest basicProfileEdit, BindingResult bindingResult) throws BadRequestException {

        return userEditProfileService.basicEditProfile(userId,  basicProfileEdit,  bindingResult);
    }

    @Operation(
            summary = "Editing information in the user's profile.",
            description = "Gets the ID of the user for whom the profile will be changed, along with the parameters of the information in the DTO that need to be changed, " +
                    "and sends it to the service for processing."
    )
    @PatchMapping(EDIT_PROFILE_INFORMATION_ABOUT_USER)
    public ResponseEntity<?> editProfileInformation(@PathVariable("user_id") Long id,
                                                    @RequestBody UpdateInformationEditProfileRequest informationEdit,
                                                    @Valid @RequestBody BindingResult bindingResult) throws BadRequestException {
        return userEditProfileService.updateProfileAboutUser(id, informationEdit, bindingResult);
    }

    @Operation(
            summary = "Creating a user's sports profile.",
            description = "Retrieves the ID of the user for whom the profile will be created, along with the information parameters in the DTO with which the profile should be created,\n" +
                    "and sends them to the service for processing."
    )
    @PostMapping(EDIT_PROFILE_BASIC_SPORT)
    public ProfileCreateResponseDto createProfile(@PathVariable("user_id") Long userId,
                                                  @Valid @RequestBody CreateProfileRequestDto profileRequestDto, BindingResult bindingResult) throws BadRequestException {
      return userEditProfileService.createProfile(userId, profileRequestDto, bindingResult);

    }

    @Operation(
            summary = "Updating a user's sports profile.",
            description = "Gets the ID of the user for whom the profile will be changed, along with the information parameters in the DTO that need to be changed,\n" +
                    "and sends them to the service for processing."
    )
    @PatchMapping(EDIT_PROFILE_BASIC_SPORT_ADDITIONALLY)
        public ResponseEntity<ProfileUpdateResponseDto> updateProfile(@PathVariable("user_id") Long userId,
                                           @RequestBody UpdateProfileRequestDto updateRequest) throws BadRequestException {
        ProfileUpdateResponseDto.ProfileUpdateResponseDtoBuilder responseBuilder = ProfileUpdateResponseDto.builder().userId(userId);

        if (updateRequest.getAthleteProfileDetails() != null) {
            log.info("Updating athlete profile: userId={}, athleteProfileId={}, request={}",
                    userId, updateRequest.getAthleteProfileDetails().getAthleteProfileId(), updateRequest.getAthleteProfileDetails());

            AthleteProfileResponseDto updatedAthleteProfile = userEditProfileService.updateAthleteProfile(
                    userId, updateRequest.getAthleteProfileDetails().getAthleteProfileId(), updateRequest.getAthleteProfileDetails());

            log.info("Updated athlete profile: {}", updatedAthleteProfile);

            responseBuilder.athleteProfiles(Set.of(updatedAthleteProfile));
        }

        if (updateRequest.getCoachProfileDetails() != null) {
            log.info("Updating coach profile: userId={}, coachProfileId={}, request={}",
                    userId, updateRequest.getCoachProfileDetails().getCoachProfileId(), updateRequest.getCoachProfileDetails());

            CoachProfileUpdateResponseDto updatedCoachProfile = userEditProfileService.updateCoachProfile(userId, updateRequest.getCoachProfileDetails().getCoachProfileId(), updateRequest.getCoachProfileDetails());
            log.info("Updated coach profile: {}", updatedCoachProfile);

            responseBuilder.coachProfiles(Set.of(updatedCoachProfile));
        }

        if (updateRequest.getJudgeProfileDetails() != null) {
            log.info("Updating coach profile: userId={}, judgeProfileId={}, request={}",
                    userId, updateRequest.getJudgeProfileDetails().getJudgeProfileId(), updateRequest.getJudgeProfileDetails());
            JudgeProfileResponseDto updatedJudgeProfile = userEditProfileService.updateJudgeProfile(userId, updateRequest.getJudgeProfileDetails().getJudgeProfileId(), updateRequest.getJudgeProfileDetails());
            log.info("Updated coach judge: {}", updatedJudgeProfile);

            responseBuilder.judgeProfiles(Set.of(updatedJudgeProfile));
        }
        ProfileUpdateResponseDto response = responseBuilder.build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Deletes the user's athletic profile.",
            description = "Gets the ID of the user for whom it will be deleted and the ID of the profile itself that will be deleted."
    )
    @DeleteMapping(DELETE_ATHLETE_PROFILE)
    public void deleteAthleteProfileByUser(@PathVariable("user_id") Long userId, @PathVariable("athlete_profile_id") Long athleteProfileId ) {
        userEditProfileService.deleteAthleteProfile(userId, athleteProfileId);
    }

    @Operation(
            summary = "Deletes the user's coach profile.",
            description = "Gets the ID of the user for whom it will be deleted and the ID of the profile itself that will be deleted."
    )
    @DeleteMapping(DELETE_COACH_PROFILE)
    public void deleteCoachProfileByUser(@PathVariable("user_id") Long userId, @PathVariable("coach_profile_id") Long coachProfileId ) {
        userEditProfileService.deleteCoachProfile(userId, coachProfileId);
    }

    @Operation(
            summary = "Deletes the user's judge profile.",
            description = "Gets the ID of the user for whom it will be deleted and the ID of the profile itself that will be deleted."
    )
    @DeleteMapping(DELETE_JUDGE_PROFILE)
    public void deleteJudgeProfileByUser(@PathVariable("user_id") Long userId, @PathVariable("judge_profile_id") Long judgeProfileId ) {
        userEditProfileService.deleteJudgeProfile(userId, judgeProfileId);
    }

    @Operation(
            summary = "Deleting a user.",
            description = "Gets the tournament grid's ID and deletes it."
    )
    @DeleteMapping(DELETE_USER)
    public void deleteUser(@PathVariable("user_id") Long userId) {
        userEditProfileService.deleteUser(userId);

    }



}
