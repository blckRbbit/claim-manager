package com.github.blckrbbit.claimmanager.util.component;

import com.github.blckrbbit.claimmanager.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseWrapper {
    private final JwtTokenUtil jwtTokenUtil;

    public <T> ResponseEntity<?> response(T subject, HttpServletRequest request, Throwable throwable) {
        return response(subject, request, HttpStatus.OK, HttpStatus.FORBIDDEN, throwable);
    }

    public <T> ResponseEntity<?> response(T subject, HttpServletRequest request, HttpStatus status,
                                          HttpStatus errorStatus, Throwable throwable) {
        if (!jwtTokenUtil.isTokenValid(jwtTokenUtil.getToken(request))) {
            return ResponseEntity.status(errorStatus).body(throwable.getMessage());
        }

        return ResponseEntity.status(status).body(subject);
    }
}
