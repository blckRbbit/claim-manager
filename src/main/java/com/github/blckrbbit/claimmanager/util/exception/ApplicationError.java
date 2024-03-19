package com.github.blckrbbit.claimmanager.util.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApplicationError {
    private int statusCode;
    private String message;
    private List<String> errorMessages;

    public ApplicationError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.errorMessages = List.of(message);
    }

    public ApplicationError(int statusCode, List<String> errorMessages) {
        this.statusCode = statusCode;
        this.message = "Some problems with the application. This is indicated in the errorMessages field.";
        this.errorMessages = errorMessages;
    }

    public ApplicationError(int statusCode, Throwable throwable) {
        this.statusCode = statusCode;
        this.message = throwable.getMessage();
        this.errorMessages = List.of(message);
    }
}
