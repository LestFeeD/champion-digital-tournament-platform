package zChampions.catalogue.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import zChampions.catalogue.entity.UserEntity;
import zChampions.catalogue.repository.UserRepository;
import zChampions.catalogue.requestDto.LoginRequest;
import zChampions.catalogue.responseDto.JwtResponse;
import zChampions.catalogue.responseDto.LoginResponse;
import zChampions.catalogue.security.config.MyUserDetails;
import zChampions.catalogue.security.jwt.JwtUtils;

@Service
@AllArgsConstructor
public class AuthUserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthUserService.class);

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        logger.info("Attempting login for email: {}", request.getEmail());

        try {

            UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() ->  {
                        logger.warn("User not found for email {}", request.getEmail());
                       return new UsernameNotFoundException("User not found");
                    });

            if (!userEntity.getEnabled()) {
                logger.warn("Login attempt for email {} failed: Email not confirmed", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("Email not confirmed", null));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getUserId(), jwt);
            logger.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(new LoginResponse("Login Successful", jwtResponse));
        } catch (
                AuthenticationException e) {
            logger.error("Login failed for email: {}", request.getEmail() );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(e.getMessage(), null));
        }
    }
}
