package zChampions.catalogue.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.email.EmailSender;
import zChampions.catalogue.entity.ConfirmationToken;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.entity.UserRoleSystem;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.factories.UserFactory;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.repository.UserRoleSystemRepository;
import zChampions.catalogue.requestDto.UpdateSignupRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTests {

    @Mock
    private UserRepository userEntityRepository;

    @Mock
    private UserFactory userDtoFactory;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private  UserRoleSystemRepository userRoleSystemRepository;

    @Mock
    private  ConfirmationTokenEmailService confirmationTokenEmailService;

    @Mock
    private ValidationErrors validationErrors;

    @Mock
    private  PasswordEncoder passwordEncoder;

    @Mock
    private  EmailSender emailSender;


    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void registerUsers_createUserWithValidParameters_returnConfirmEmail() throws BadRequestException {
        String encodedPassword = "testPassword";

        Long userId = 1L;

        UpdateSignupRequest signupRequest = new UpdateSignupRequest();
        signupRequest.setFirstName("testName");
        signupRequest.setPassword("testPassword");
        signupRequest.setEmail("testEmail@gmail.com");

        UserEntity user = UserEntity.builder()
                .userId(userId)
                .firstName(signupRequest.getFirstName())
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .enabled(false)
                .build();

        UserRoleSystem userRoleSystem = new UserRoleSystem();
        userRoleSystem.setUserRole(zChampions.catalogue.enumsEntities.UserRoleSystem.USER);
        user.setUserRoleSystemEntities(new HashSet<>( Collections.singletonList(userRoleSystem)));


        when(bindingResult.hasErrors()).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(encodedPassword);
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(user);
        when(userRoleSystemRepository.save(any(UserRoleSystem.class))).thenReturn(userRoleSystem);

         userRegistrationService.registerUsers(signupRequest, bindingResult);

        verify(passwordEncoder, times(1)).encode(signupRequest.getPassword());
        verify(userRoleSystemRepository, times(1)).save(any(UserRoleSystem.class));
        verify(userEntityRepository, times(1)).save(any(UserEntity.class));
        verify(emailSender, times(1)).send(eq(signupRequest.getEmail()), anyString());
    }

    @Test
    void registerUsers_createUserWithValidationErrors_returnBadRequestException() throws BadRequestException {
        UpdateSignupRequest signupRequest = new UpdateSignupRequest();

        when(bindingResult.hasErrors()).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userRegistrationService.registerUsers(signupRequest, bindingResult));

        verify(passwordEncoder, never()).encode(signupRequest.getPassword());
        verify(userRoleSystemRepository, never()).save(any(UserRoleSystem.class));
        verify(userEntityRepository, never()).saveAndFlush(any(UserEntity.class));
        verify(emailSender, never()).send(eq(signupRequest.getEmail()), anyString());
    }

    @Test
    void confirmToken_confirmTokenByUser_returnStringConfirmed() {
        String token = "testToken";
        Long tokenId = 1L;
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15) );
        confirmationToken.setTokenId(tokenId);

        UserEntity user = new UserEntity();
        user.setEmail("testEmail");
        user.setConfirmationTokens(Collections.singletonList(confirmationToken));
        confirmationToken.setUser(user);


        when(confirmationTokenEmailService.getToken(token)).thenReturn(Optional.of(confirmationToken));
        when(confirmationTokenEmailService.setConfirmedAt(token)).thenReturn(1);
        when(confirmationTokenEmailService.enableAppUser("testEmail")).thenReturn(1);

        userRegistrationService.confirmToken(token);

        verify(confirmationTokenEmailService, times(1)).getToken(token);
        verify(confirmationTokenEmailService, times(1)).setConfirmedAt(token);
        verify(confirmationTokenEmailService, times(1)).enableAppUser("testEmail");

    }

    @Test
    void confirmToken_emailAlreadyConfirmed_returnIllegalStateException() {
        String token = "testToken";
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        when(confirmationTokenEmailService.getToken(token)).thenReturn(Optional.of(confirmationToken));

        assertThrows(IllegalStateException.class, () -> userRegistrationService.confirmToken(token));
    }

    @Test
    void confirmToken_tokenExpired_returnIllegalStateException() {
        String token = "testToken";
        Long tokenId = 1L;
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(1) );
        confirmationToken.setTokenId(tokenId);

        when(confirmationTokenEmailService.getToken(token)).thenReturn(Optional.of(confirmationToken));

        assertThrows(IllegalStateException.class, () -> userRegistrationService.confirmToken(token));

    }

    }

