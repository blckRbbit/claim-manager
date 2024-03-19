package com.github.blckrbbit.claimmanager.service;

import com.github.blckrbbit.claimmanager.repository.RoleRepository;
import com.github.blckrbbit.claimmanager.repository.entity.Role;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public Role getRole(String role) {
        role = role.toUpperCase();
        return repository.findByName(role).orElseThrow(ResourceNotFoundException::new);
    }
}
