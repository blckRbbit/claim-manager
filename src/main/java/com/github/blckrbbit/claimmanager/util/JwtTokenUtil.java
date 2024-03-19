package com.github.blckrbbit.claimmanager.util;

import com.github.blckrbbit.claimmanager.repository.TokenRepository;
import com.github.blckrbbit.claimmanager.repository.entity.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Integer lifeTime;
    private final TokenRepository repository;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", rolesList);
        claims.put("login", userDetails.getUsername());

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifeTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Transactional
    public Token destroyToken(String token) {
        Token expiredToken = new Token(token);
        repository.save(new Token(token));
        log.info("Expired token: {}", expiredToken.getToken());
        return expiredToken;
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public List<String> getRoles(String token) {
        return getClaimFromToken(token, (Function<Claims, List<String>>)
                claims -> claims.get("roles", List.class));
    }

    public String getLogin(String token) {
        return getClaimFromToken(token, claims -> claims.get("login", String.class));
    }

    public String getPhone(String token) {
        return getClaimFromToken(token, claims -> claims.get("phone", String.class));
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public boolean isTokenValid(String token) {
        return repository.getByToken(token) == null;
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
