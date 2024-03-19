package com.github.blckrbbit.claimmanager.controller;

import com.github.blckrbbit.claimmanager.config.IntegrationBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClaimControllerTest extends IntegrationBaseTest {

    @Test
    void create_draft_claim_when_credentials_is_right() throws Exception {
        String token = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(post("/api/v1/claims/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(
                                "test topic of claim"
                        )
                )
                .andDo(print())
                .andExpect(jsonPath("$").isString())
                .andExpect(status().isOk()
                );
    }

    @Test
    void create_draft_claim_when_credentials_is_wrong() throws Exception {
        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(post("/api/v1/claims/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                        .content(
                                "test topic of claim"
                        )
                )
                .andExpect(status().isForbidden()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(post("/api/v1/claims/draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                        .content(
                                "test topic of claim"
                        )
                )
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void send_claim_when_credentials_is_right() throws Exception {
        String token = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(
                                "test topic of claim"
                        )
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$", hasKey("status")))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(status().isOk()
                );
    }

    @Test
    void send_claim_when_credentials_is_wrong() throws Exception {
        String token = getToken("user1", PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(
                                "test topic of claim"
                        )
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(jsonPath("$.errorMessages").value("Resource not found"))
                .andExpect(status().isNotFound()
                );

        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                        .content(
                                "test topic of claim"
                        )
                )

                .andExpect(status().isForbidden()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                        .content(
                                "test topic of claim"
                        )
                )
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void read_owner_claims_when_credentials_is_right() throws Exception {
        String token = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("content")))
                .andExpect(jsonPath("$", hasKey("pageable")))
                .andExpect(jsonPath("$", hasKey("empty")))
                .andExpect(jsonPath("$", hasKey("numberOfElements")))
                .andExpect(jsonPath("$", hasKey("size")))
                .andExpect(jsonPath("$", hasKey("totalPages")))
                .andExpect(jsonPath("$", hasKey("totalElements")))
                .andExpect(status().isOk()
                );
    }

    @Test
    void edit_claim_when_credentials_is_right() throws Exception {
        String token = getToken(USER_LOGIN, PASSWORD);
        String edit = "new text for claim";
        mvc
                .perform(put("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(edit)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$", hasKey("description")))
                .andExpect(jsonPath("$.description").value(edit))
                .andExpect(status().isOk()
                );
    }

    @Test
    void edit_claim_when_credentials_is_wrong() throws Exception {
        String token = getToken("user1", PASSWORD);
        String edit = "new text for claim";
        mvc
                .perform(put("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(edit)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").value("Resource not found"))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(status().isNotFound()
                );

        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);
        mvc
                .perform(put("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                        .content(edit)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").value("Resource not found"))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(status().isNotFound()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);
        mvc
                .perform(put("/api/v1/claims/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                        .content(edit)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("statusCode")))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$", hasKey("message")))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$", hasKey("errorMessages")))
                .andExpect(jsonPath("$.errorMessages").value("Resource not found"))
                .andExpect(jsonPath("$.errorMessages").isArray())
                .andExpect(status().isNotFound()
                );
    }

    @Test
    void get_sent_claim_when_credentials_is_right() throws Exception {
        String token = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/sent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("content")))
                .andExpect(jsonPath("$", hasKey("pageable")))
                .andExpect(jsonPath("$", hasKey("empty")))
                .andExpect(jsonPath("$", hasKey("numberOfElements")))
                .andExpect(jsonPath("$", hasKey("size")))
                .andExpect(jsonPath("$", hasKey("totalPages")))
                .andExpect(jsonPath("$", hasKey("totalElements")))
                .andExpect(status().isOk()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/sent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("content")))
                .andExpect(jsonPath("$", hasKey("pageable")))
                .andExpect(jsonPath("$", hasKey("empty")))
                .andExpect(jsonPath("$", hasKey("numberOfElements")))
                .andExpect(jsonPath("$", hasKey("size")))
                .andExpect(jsonPath("$", hasKey("totalPages")))
                .andExpect(jsonPath("$", hasKey("totalElements")))
                .andExpect(status().isOk()
                );
    }

    @Test
    void get_sent_claim_when_credentials_is_wrong() throws Exception {
        String token = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/sent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void accept_claim_when_credentials_is_right() throws Exception {
        String token = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/accept/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$", hasKey("status")))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(status().isOk()
                );

    }

    @Test
    void accept_claim_when_credentials_is_wrong() throws Exception {
        String userToken = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/accept/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", userToken)
                )
                .andExpect(status().isForbidden()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/accept/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void reject_claim_when_credentials_is_right() throws Exception {
        String token = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/decline/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$", hasKey("status")))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(status().isOk()
                );

    }

    @Test
    void reject_claim_when_credentials_is_wrong() throws Exception {
        String userToken = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/decline/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", userToken)
                )
                .andExpect(status().isForbidden()
                );

        String adminToken = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(patch("/api/v1/claims/accept/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", adminToken)
                )
                .andExpect(status().isForbidden()
                );
    }

    @Test
    void get_processing_claim_when_credentials_is_right() throws Exception {
        String token = getToken(ADMIN_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/processing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                )
                .andExpect(jsonPath("$", hasKey("content")))
                .andExpect(jsonPath("$", hasKey("pageable")))
                .andExpect(jsonPath("$", hasKey("empty")))
                .andExpect(jsonPath("$", hasKey("numberOfElements")))
                .andExpect(jsonPath("$", hasKey("size")))
                .andExpect(jsonPath("$", hasKey("totalPages")))
                .andExpect(jsonPath("$", hasKey("totalElements")))
                .andExpect(status().isOk()
                );

    }

    @Test
    void get_processing_claim_when_credentials_is_wrong() throws Exception {
        String userToken = getToken(USER_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/processing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", userToken)
                )
                .andExpect(status().isForbidden()
                );

        String operatorToken = getToken(OPERATOR_LOGIN, PASSWORD);

        mvc
                .perform(get("/api/v1/claims/processing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", operatorToken)
                )
                .andExpect(status().isForbidden()
                );
    }
}
