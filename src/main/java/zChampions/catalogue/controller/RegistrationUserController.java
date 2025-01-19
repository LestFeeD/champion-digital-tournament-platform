package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import zChampions.catalogue.requestDto.UpdateSignupRequest;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.service.UserRegistrationService;

@Tag(name = "class working by user registration")
@RestController
@RequiredArgsConstructor
@Transactional
public class RegistrationUserController {

    private  final UserRegistrationService userRegistrationService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationUserController.class);

    public static final String CREATE_USER = "/registration";
    public static final String CONFIRM_USER = "/registration/confirm";

    @Operation(
            summary = "User registration on the service.",
            description = "It writes the specified parameters to the dto, passes them to the service and writes them to the class, saves them, and returns them."
    )
    @PostMapping(CREATE_USER)
    public String registrationUser (@RequestBody @Valid UpdateSignupRequest signupRequest,
                                             BindingResult bindingResult) throws BadRequestException  {

        return userRegistrationService.registerUsers(
                signupRequest,
                bindingResult

        );
    }

    @Operation(
            summary = "Sends the token to the email address for its confirmation.",
            description = "It writes the specified parameters to the dto, passes them to the service and writes them to the class, saves them, and returns them."
    )
    @GetMapping(CONFIRM_USER)
    public String confirm(@RequestParam("token") String token) {
        return userRegistrationService.confirmToken(token);
    }

}
