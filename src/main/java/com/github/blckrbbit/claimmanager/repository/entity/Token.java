package com.github.blckrbbit.claimmanager.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Getter
@Entity(name = "tokens")
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String token;

    public Token(String token) {
        this.token = token;
    }
}
