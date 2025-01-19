package zChampions.catalogue.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import zChampions.catalogue.repository.*;
import zChampions.catalogue.requestDto.UpdateSignupRequest;
import zChampions.catalogue.email.EmailSender;
import zChampions.catalogue.entity.ConfirmationToken;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.entity.UserRoleSystem;
import zChampions.catalogue.exceptions.BadRequestException;
import zChampions.catalogue.factories.UserFactory;
import zChampions.catalogue.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class UserRegistrationService {

    private final UserRepository userEntityRepository;
    private final UserFactory userDtoFactory;
    private final UserOrganizationRoleRepository organizationRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleSystemRepository userRoleSystemRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ConfirmationTokenEmailService confirmationTokenEmailService;
    private final EmailSender emailSender;
    private final ConfirmationTokenEmailRepository confirmationTokenEmailRepository;
    private final ApplicationRepository applicationRepository;
    private final ResultStandingsRepository resultStandingsRepository;
    private final EventRepository eventRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);
    private final ValidationErrors validationErrors;


@Transactional
    public String registerUsers(@Valid UpdateSignupRequest signupRequest,
                                         BindingResult bindingResult) throws BadRequestException {
        logger.info("Register user with parameters: firstName {}, email {}, password {}",
                signupRequest.getFirstName(), signupRequest.getEmail(), signupRequest.getPassword());
        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while register user: {}", result);
            throw new BadRequestException(result);

        }
        Long emailFound = userEntityRepository.emailFound(signupRequest.getEmail());
    boolean hasAccess = emailFound > 0;

    if(hasAccess) {
        logger.warn("Such mail already exists {}", signupRequest.getEmail());
        throw new BadRequestException("A user with such a mail already exists.");
    }
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserEntity entity = UserEntity.builder()
                .firstName(signupRequest.getFirstName())
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .enabled(false)
                .build();

        UserRoleSystem userRoleSystem = new UserRoleSystem();
        userRoleSystem.setUserRole(zChampions.catalogue.enumsEntities.UserRoleSystem.USER);
    entity.setUserRoleSystemEntities(new HashSet<>(Collections.singletonList(userRoleSystem)));
        userRoleSystemRepository.save(userRoleSystem);


    ConfirmationToken confirmationToken = createConfirmationToken(entity);

    userEntityRepository.save(entity);
    logger.info("Successfully create user with ID: {}", entity.getUserId());

    sendConfirmationEmail(signupRequest, confirmationToken.getToken());
        return "Confirmation email sent to " + signupRequest.getEmail() + ". Please confirm your account.";

    }

    private ConfirmationToken createConfirmationToken(UserEntity user) {
        String token = UUID.randomUUID().toString();


        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        logger.info("Saving confirmation token: {}", user.getUserId());
        try {
        confirmationToken.setUser(user);
            user.setConfirmationTokens(new ArrayList<>(List.of(confirmationToken)));

        confirmationTokenEmailRepository.save(confirmationToken);
            logger.info("Confirmation token saved successfully for user with id: {}",  user.getUserId());
        } catch (Exception e){
            logger.error("Failed to save confirmation token for user: {}", e.getMessage());

        }


        return confirmationToken;
    }

    private void sendConfirmationEmail(UpdateSignupRequest signupRequest, String token) {
        String link = "http://localhost:1212/api/registration/confirm?token=" + token;
        logger.info("Sending confirmation email to: {}", signupRequest.getEmail());
        emailSender.send(signupRequest.getEmail(), buildEmail(signupRequest.getFirstName(), link));
    }


    @Transactional
    @Scheduled(fixedRate = 900000,  initialDelay = 5000)
    public void removeExpiredUsers() {
        logger.info("Scheduled task running...");

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        logger.info("Current timestamp: {}", now);

        List<Long> expiredTokens = confirmationTokenEmailRepository.tokenIds(timestamp);
        logger.info("Expired token ID: {}", expiredTokens);

        for (Long tokenId : expiredTokens) {

            Long userId = userEntityRepository.tokenUsers(tokenId);


            if (userId != null) {
                confirmationTokenEmailRepository.deleteById(tokenId);

                userEntityRepository.deleteById(userId);
                logger.info("Deleted expired user with ID: {}", userId);
            }
        }
            }



    @Transactional
    public String confirmToken(String token) {
        logger.info("Accepted token: {}", token);

                ConfirmationToken confirmationToken = confirmationTokenEmailService
                .getToken(token)
                .orElseThrow(() -> {
                    logger.error("Token not found: {}", token);
                   return new IllegalStateException("token not found");

                });

        if (confirmationToken.getConfirmedAt() != null) {
            logger.warn("Email already confirmed for token: {}", token);
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            logger.warn("Token expired: {}", token);
            throw new IllegalStateException("token expired");
        }


        UserEntity userEntity = confirmationToken.getUser();
        userEntity.setEnabled(true);

        confirmationTokenEmailService.setConfirmedAt(token);

        confirmationTokenEmailService.enableAppUser(
                userEntity.getEmail());

        logger.info("Token successfully confirmed: {}", token);
        return "confirmed";
    }


    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
