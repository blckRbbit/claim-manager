package com.github.blckrbbit.claimmanager.service;

import com.github.blckrbbit.claimmanager.dto.UserDTO;
import com.github.blckrbbit.claimmanager.repository.UserRepository;
import com.github.blckrbbit.claimmanager.repository.entity.Role;
import com.github.blckrbbit.claimmanager.repository.entity.User;
import com.github.blckrbbit.claimmanager.util.JwtTokenUtil;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.CredentialsException;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.ResourceNotFoundException;
import com.github.blckrbbit.claimmanager.util.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService  implements UserDetailsService {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final RoleService roleService;

    @Value("${app.links.register}")
    private String registerLink;

    public Collection<UserDTO> getUsers() {
        return repository.findAll().stream().map(mapper::toModel).toList();
    }

    public UserDTO changeRights(HttpServletRequest request, Long userId, String roleName) {
        roleName = roleName.toUpperCase();
        Role role = roleService.getRole(roleName);

        Role userRole = roleService.getRole("USER");
        Role operatorRole = roleService.getRole("OPERATOR");
        Role adminRole = roleService.getRole("ADMIN");

        User currentUser = getUser(request);
        User user = repository.findById(userId).orElseThrow(ResourceNotFoundException::new);

        if (currentUser.equals(user)) {
            throw new UnsupportedOperationException("Cannot assign permissions to yourself");
        }

        Collection<Role> roles = user.getRoles();

        if(role.equals(adminRole)) {
            throw new CredentialsException("Insufficient rights to assign the administrator role");
        }

        if (role.equals(operatorRole)) {
            roles.remove(userRole);
        }

        if (role.equals(userRole)) {
            roles.clear();
        }

        roles.add(role);
        return mapper.toModel(repository.save(user));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = getUser(login);
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    public User getUser(HttpServletRequest request) {
        String login = jwtTokenUtil.getLogin(jwtTokenUtil.getToken(request));
        return getUser(login);
    }

    private User getUser(String login) {
        return repository.findByLogin(login).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format("User %s not found", login)
                )
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        role.getName()))
                .collect(Collectors.toList());
    }
}
