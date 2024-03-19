package com.github.blckrbbit.claimmanager.controller;

import com.github.blckrbbit.claimmanager.config.IntegrationBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerTest extends IntegrationBaseTest {
    @Test
    void get_auth_token_when_data_is_valid() throws Exception {
        mvc
                .perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "login": "admin",
                                                "password": "100"
                                            }
                                        """
                        )
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("token")))
                .andExpect(status().isOk()
                );
    }

    @Test
    void get_auth_token_when_data_is_invalid() throws Exception {
        mvc
                .perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "login": "admin11",
                                                "password": "100"
                                            }
                                        """
                        )
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("User admin11 not found"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages").value("User admin11 not found"))
                .andExpect(status().is4xxClientError()
                );

        mvc
                .perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                            {
                                                "login": "user",
                                                "password": "101"
                                            }
                                        """
                        )
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message")
                        .value("Some problems with the application. This is indicated in the errorMessages field."))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages").value(containsInAnyOrder("Incorrect login or password",
                        "Login or register using the link: http://localhost:8387/api/v1/users/login")))
                .andExpect(status().is4xxClientError()
                );
    }

    @Test
    void check_logout() throws Exception {
        String token = getToken(ADMIN_LOGIN, PASSWORD);
        mvc
                .perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(status().isOk()
                );

        mvc
                .perform(get("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection()
                );
    }

    @Test
    void get_users_when_data_is_valid() throws Exception {
        String token = getToken(ADMIN_LOGIN, PASSWORD);
        mvc
                .perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
                .andExpect(status().isOk()
                );
    }

    @Test
    void get_users_when_not_enough_rights() throws Exception {
        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);
        mvc
                .perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                )
                .andDo(print())
                .andExpect(status().isForbidden()
                );

        String userToken = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", userToken)
                )
                .andDo(print())
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void change_user_rights() throws Exception {
        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);
        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);
        String userToken = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/users/3?role=operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$", hasKey("roles")))
                .andExpect(content()
                        .string(containsString("{\"id\":3,\"login\":\"user\",\"surname\":\"Sidorov\",\"name\":\"Sidor\",\"phone\":\"333\",\"roles\":[{\"id\":2,\"name\":\"OPERATOR\",\"authority\":\"OPERATOR\"}]}")))
                .andExpect(status().isOk()
                );

        mvc
                .perform(patch("/api/v1/users/1?role=operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Cannot assign permissions to yourself"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").value("Cannot assign permissions to yourself"))
                .andExpect(status().isForbidden()
                );

        mvc
                .perform(patch("/api/v1/users/3?role=admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Insufficient rights to assign the administrator role"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").value("Insufficient rights to assign the administrator role"))
                .andExpect(status().isForbidden()
                );

        mvc
                .perform(patch("/api/v1/users/3?role=operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                )
                .andExpect(status().isForbidden()
                );

        mvc
                .perform(patch("/api/v1/users/3?role=operator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", userToken)
                )
                .andExpect(status().isForbidden()
                );
    }

}
