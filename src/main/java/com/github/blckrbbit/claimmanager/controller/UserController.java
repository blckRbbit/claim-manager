package com.github.blckrbbit.claimmanager.controller;

import com.github.blckrbbit.claimmanager.service.UserService;
import com.github.blckrbbit.claimmanager.util.JwtTokenUtil;
import com.github.blckrbbit.claimmanager.util.component.JwtRequest;
import com.github.blckrbbit.claimmanager.util.component.JwtResponse;
import com.github.blckrbbit.claimmanager.util.component.ResponseWrapper;
import com.github.blckrbbit.claimmanager.util.exception.ApplicationError;
import com.github.blckrbbit.claimmanager.util.exception.exceptions.CredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final ResponseWrapper responseWrapper;
    private final UserService service;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${app.links.register}")
    private String registerLink;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new ApplicationError(HttpStatus.UNAUTHORIZED.value(),
                            List.of(
                                    "Incorrect login or password",
                                    String.format("Login or register using the link: %s", registerLink)
                            )), HttpStatus.UNAUTHORIZED
            );
        }
        UserDetails userDetails =
                service.loadUserByUsername(authRequest.getLogin());

        String token = jwtTokenUtil.generateToken(userDetails);
        log.info("login: {}, rights: {}", jwtTokenUtil.getLogin(token), jwtTokenUtil.getRoles(token));
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        return responseWrapper.response(service.getUsers(), request, HttpStatus.OK,
                HttpStatus.FORBIDDEN, new CredentialsException());
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("callback");
    }

    @GetMapping("/logout/callback")
    public ResponseEntity<?> logoutCallback(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.startsWith("Bearer") ? header.substring(7) : header;
        jwtTokenUtil.destroyToken(token);
        return ResponseEntity.ok("You have successfully logged out of your account");
    }

    @Transactional
    @PatchMapping("/{userId}")
    public ResponseEntity<?> changeRights(HttpServletRequest request,
                                          @PathVariable Long userId, @RequestParam String role) {
        return responseWrapper
                .response(service.changeRights(request, userId, role), request, new UnsupportedOperationException());
    }

}
