package com.github.blckrbbit.claimmanager.dto;

import com.github.blckrbbit.claimmanager.repository.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String login;
    private String surname;
    private String name;
    private String phone;
    private Collection<Role> roles;
}
