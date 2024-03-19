package com.github.blckrbbit.claimmanager.util.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.blckrbbit.claimmanager.util.exception.ApplicationError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        objectMapper.writeValue(response.getOutputStream(),
                new ApplicationError(HttpStatus.FORBIDDEN.value(),
                        List.of(authException.getLocalizedMessage(), "Not enough rights")));

        log.error("Authentication error: {}", authException.getMessage());
        log.error("Causes: {}", Arrays.toString(authException.getStackTrace()));
    }
}
