package com.github.blckrbbit.claimmanager.repository;

import com.github.blckrbbit.claimmanager.repository.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    Token getByToken(String token);
}
