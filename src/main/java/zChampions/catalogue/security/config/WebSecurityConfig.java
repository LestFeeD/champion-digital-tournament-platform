package zChampions.catalogue.security.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zChampions.catalogue.security.jwt.JwtAuthEntryPoint;
import zChampions.catalogue.security.jwt.JwtUtils;
import zChampions.catalogue.security.jwt.TokenFilter;
import zChampions.catalogue.service.MyUserDetailsService;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    
    @Autowired
    public MyUserDetailsService userService;

    public JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public TokenFilter tokenFilter(JwtUtils jwtUtils, UserDetailsService customUserDetailsService){
        return new TokenFilter(jwtUtils, customUserDetailsService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,JwtUtils jwtUtils, UserDetailsService customUserDetailsService) throws Exception {

         httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->  auth.requestMatchers("/api/login/").permitAll()
                        .requestMatchers("/api/add-registration/**").permitAll()
                        .requestMatchers("/api/edit-profile/**").authenticated()
                        .requestMatchers("/api/cabinet/**").authenticated()
                        .requestMatchers("/api/add-organization/**").authenticated()
                        .requestMatchers("/api/edit-options/event/**").authenticated()
                        .requestMatchers("/api/edit-organization/**").authenticated()
                        .requestMatchers("/api/edit-organization/**").authenticated()
                        .requestMatchers("/api/manage-registration/").authenticated()
                        .requestMatchers("/app/edit-options/**").authenticated()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html#/**").permitAll()
                        .requestMatchers("/swagger-ui.html/**").permitAll()
                        .requestMatchers("/swagger-ui-custom.html/**").permitAll()
                        .requestMatchers("/v1/swagger-ui.html").permitAll()




                        .requestMatchers("/error").permitAll());

        httpSecurity.authenticationProvider(authenticationProvider())
                .addFilterBefore(tokenFilter(jwtUtils, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint));

                return httpSecurity.build();

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
