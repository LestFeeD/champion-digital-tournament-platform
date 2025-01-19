package zChampions.catalogue.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zChampions.catalogue.entity.ConfirmationToken;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.repository.ConfirmationTokenEmailRepository;
import zChampions.catalogue.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConfirmationTokenEmailService {
    private final ConfirmationTokenEmailRepository confirmationTokenEmailRepository;
    private final UserRepository userEntityRepository;
    private static final Logger logger = LoggerFactory.getLogger(ConfirmationTokenEmailService.class);



    public Optional<ConfirmationToken> getToken(String token) {
        logger.info("Retrieving confirmation token: {}", token);

        Optional<ConfirmationToken> tokenOptional = confirmationTokenEmailRepository.findByToken(token);

        if(tokenOptional.isPresent()){

            Long userId = tokenOptional.get().getUser().getUserId();

            logger.info("Confirmation token found for token: {} with user id: {}", token, userId);
        } else {
            logger.warn("Confirmation token not found for token: {}", token);
        }
        return tokenOptional;
    }

    public int setConfirmedAt(String token) {
        logger.info("Setting confirmation time for token: {}", token);
        int rowUpdated =  confirmationTokenEmailRepository.updateConfirmedAt(
                token, LocalDateTime.now());
        if(rowUpdated > 0) {
            logger.info("Confirmation time set successfully for token: {}", token);

        } else {
            logger.warn("Failed to set confirmation time for token: {}",token);
        }
        return rowUpdated;
    }

    public int enableAppUser(String email) {
        logger.info("Enabling user with email: {}", email);
        int rowUpdated = userEntityRepository.enableAppUser(email);
        if(rowUpdated > 0 ) {
            logger.info("User enabled successfully with email: {}", email);
        } else {
            logger.warn("Failed to enable user with email: {}", email);
        }
        return rowUpdated;
    }
}
