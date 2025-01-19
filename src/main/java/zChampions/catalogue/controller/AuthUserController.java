package zChampions.catalogue.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zChampions.catalogue.requestDto.LoginRequest;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.responseDto.LoginResponse;
import zChampions.catalogue.security.jwt.JwtUtils;
import zChampions.catalogue.service.AuthUserService;
import zChampions.catalogue.service.UserRegistrationService;

@Tag(name = "class working by user authentication")
@RestController
@RequiredArgsConstructor
@Transactional
public class AuthUserController {

    private  final UserRegistrationService userRegistrationService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthUserService authUserService;

    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    public static final String LOGIN_USER = "/login";

    @Operation(
            summary = "User authentication.",
            description = "Passes the DTO parameters and passes the information to the service."
    )
    @PostMapping(LOGIN_USER)
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest request) {
    return authUserService.login(request);
    }

}
