package com.github.blckrbbit.claimmanager.repository.entity.support;

import com.github.blckrbbit.claimmanager.util.exception.exceptions.ResourceNotFoundException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

public enum RolesNames implements GrantedAuthority {
    USER, OPERATOR, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

}
