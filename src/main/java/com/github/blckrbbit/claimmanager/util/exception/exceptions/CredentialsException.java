package com.github.blckrbbit.claimmanager.util.exception.exceptions;

public class CredentialsException extends IllegalArgumentException{
    public CredentialsException() {
        super("Not enough rights");
    }

    public CredentialsException(String message) {
        super(message);
    }
}
