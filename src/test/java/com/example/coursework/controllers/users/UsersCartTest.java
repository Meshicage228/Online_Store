/*
package com.example.coursework.controllers.users;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import com.example.coursework.controllers.admin.AdminUsersController;
import com.example.coursework.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = UsersCart.class)
@AutoConfigureMockMvc(addFilters = false)
class UsersCartTest {

    @MockBean
    private UsersClient client;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService service;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setUp() {
        TokenGenerationFilter tokenGenerationFilter = new TokenGenerationFilter(service, encoder);
        TokenValidationFilter validationFilter = new TokenValidationFilter(service);
    }


    @Test
    void getCart() {
    }

    @Test
    void createPurchase() {
    }

    @Test
    void changeCount() {
    }

    @Test
    void deleteFromCart() {
    }

    @Test
    void addToCart() {
    }
}*/
