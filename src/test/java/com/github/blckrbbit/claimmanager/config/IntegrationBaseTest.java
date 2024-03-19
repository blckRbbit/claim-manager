package com.github.blckrbbit.claimmanager.config;

import com.github.blckrbbit.claimmanager.config.init.PostgreSQLInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {
        PostgreSQLInitializer.Initializer.class
})
public abstract class IntegrationBaseTest {

    protected final String ADMIN_LOGIN = "admin";
    protected final String OPERATOR_LOGIN = "operator";
    protected final String USER_LOGIN = "user";
    protected final String PASSWORD = "100";

    @Autowired
    protected MockMvc mvc;

    @BeforeAll
    static void init() {
        PostgreSQLInitializer.container.start();
    }

    protected String getToken(String login, String password) throws Exception {
        String body = String.format("{ \"login\":\"%s\", \"password\":\"%s\" }", login, password);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        return response.split(":")[1].replace("\"", "").replace("}", "").trim();
    }
}
