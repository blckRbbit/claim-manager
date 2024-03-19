package com.github.blckrbbit.claimmanager.config;

import com.github.blckrbbit.claimmanager.service.UserService;
import com.github.blckrbbit.claimmanager.util.exception.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.github.blckrbbit.claimmanager.repository.entity.support.RolesNames.*;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService service;
    private final JwtRequestFilter jwtRequestFilter;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(service).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public WebMvcConfigurer corsConfiguration() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("http://localhost")
                        .allowedMethods("*");
            }
        };
    }

    @Bean
    public RestAuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> corsConfiguration())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/logout/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/claims").hasAuthority(USER.getAuthority())

                        .requestMatchers(HttpMethod.GET, "/api/v1/claims/sent")
                        .hasAnyAuthority(ADMIN.getAuthority(), OPERATOR.getAuthority())

                        .requestMatchers(HttpMethod.GET, "/api/v1/claims/processing").hasAuthority(ADMIN.getAuthority())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/claims/accept/{id}")
                        .hasAuthority(OPERATOR.getAuthority())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/claims/decline/{id}")
                        .hasAuthority(OPERATOR.getAuthority())

                        .requestMatchers(HttpMethod.POST, "/api/v1/claims/draft").hasAuthority(USER.getAuthority())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/claims/{id}").hasAuthority(USER.getAuthority())

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{id}").hasAuthority(ADMIN.getAuthority())

                        .requestMatchers(HttpMethod.GET, "/api/v1/users").hasAuthority(ADMIN.getAuthority())

                        .anyRequest().authenticated())

                .logout(
                        logout -> logout.logoutUrl("/api/v1/users/logout")
                                .logoutSuccessUrl("/api/v1/users/logout/callback")
                                .deleteCookies("JSESSIONID")
                                .permitAll()

                )
                .authenticationManager(authenticationManager)
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(authenticationEntryPoint())
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

}
