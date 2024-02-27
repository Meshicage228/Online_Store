package com.example.userblservice.controllers;

import com.example.userblservice.entity.user.UsersCart;
import com.example.userblservice.exceptions.handler.UserExceptionHandler;
import com.example.userblservice.repository.user.CartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @BeforeAll
    public static void setUp() {
        UserExceptionHandler productExceptionHandler = new UserExceptionHandler();
    }

    @Test
    @Sql(value = "classpath:/data/cart/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cart/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void changeCountIncrement() throws Exception {
        mockMvc.perform(patch("/v1/users/cart/{cart_id}/changeCount", 2)
                .param("option", "increment"));

        Optional<UsersCart> incremented = cartRepository.findById(2);

        assertTrue(incremented.isPresent());
        Assertions.assertThat(incremented.get().getCountToBuy()).isEqualTo(6);
    }
    @Test
    @Sql(value = "classpath:/data/cart/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cart/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void changeCountDecrement() throws Exception {
        mockMvc.perform(patch("/v1/users/cart/{cart_id}/changeCount", 2)
                .param("option", "decrement"));

        Optional<UsersCart> decremented = cartRepository.findById(2);

        assertTrue(decremented.isPresent());
        Assertions.assertThat(decremented.get().getCountToBuy()).isEqualTo(4);
    }
    @Test
    @Sql(value = "classpath:/data/cart/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cart/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void addToCart() throws Exception {
        mockMvc.perform(post("/v1/users/cart/{user_id}/{prod_id}",
                        UUID.fromString("5cfdb965-eb0b-40ed-97be-746c63dbc73e"), 2));

        Optional<UsersCart> byId = cartRepository.findById(1);

        assertTrue(byId.isPresent());
    }
}