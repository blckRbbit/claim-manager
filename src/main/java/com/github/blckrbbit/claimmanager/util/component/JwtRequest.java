package com.github.blckrbbit.claimmanager.util.component;

import lombok.Data;

@Data
public class JwtRequest {
    private String login;
    private String password;
}
