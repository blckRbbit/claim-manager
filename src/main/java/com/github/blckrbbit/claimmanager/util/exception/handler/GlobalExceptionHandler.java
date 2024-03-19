package com.github.blckrbbit.claimmanager.util.exception.handler;

import com.github.blckrbbit.claimmanager.util.exception.ApplicationError;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ClaimStatusException;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.CredentialsException;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApplicationError> catchResourceNotFoundException(ResourceNotFoundException e) {
        log.error(String.valueOf(e));
        return new ResponseEntity<>(new ApplicationError(HttpStatus.NOT_FOUND.value(), e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CredentialsException.class)
    public ResponseEntity<ApplicationError> catchCredentialsException(CredentialsException e) {
        log.error(String.valueOf(e));
        return new ResponseEntity<>(new ApplicationError(HttpStatus.FORBIDDEN.value(), e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ClaimStatusException.class)
    public ResponseEntity<ApplicationError> catchClaimStatusException(ClaimStatusException e) {
        log.error(String.valueOf(e));
        return new ResponseEntity<>(new ApplicationError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()),
                HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApplicationError> catchUnsupportedOperationException(UnsupportedOperationException e) {
        log.error(String.valueOf(e));
        return new ResponseEntity<>(new ApplicationError(HttpStatus.FORBIDDEN.value(), e.getMessage()),
                HttpStatus.FORBIDDEN);
    }

}
