package com.github.blckrbbit.claimmanager.util.component;

public enum ClaimStatus {
    DRAFT("draft"), SENT("sent"), ACCEPTED("accepted"), REJECTED("rejected");

    private final String value;

    ClaimStatus(String value) {
        this.value = value;
    }
}
