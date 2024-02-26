package com.example.userblservice.controllers;

import com.example.userblservice.entity.user.UsersCart;
import com.example.userblservice.exceptions.handler.UserExceptionHandler;
import com.example.userblservice.repository.user.CartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() {
        UserExceptionHandler productExceptionHandler = new UserExceptionHandler();
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('aaf5f4bb-1094-4752-a6a3-d2c415425c30')")
    @Sql(statements = "INSERT INTO products (product_id) values (12)")
    @Sql(statements = "INSERT INTO user_carts (cart_id, count_to_buy, product_id, user_id) values (5, 5, 12, 'aaf5f4bb-1094-4752-a6a3-d2c415425c30')")
    void changeCountIncrement() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/users/cart/{cart_id}/changeCount", 5)
                .param("option", "increment"));

        Optional<UsersCart> incremented = cartRepository.findById(5);

        Assertions.assertThat(incremented.get().getCountToBuy()).isEqualTo(6);
    }
    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('5cfdb965-eb0b-40ed-97be-746c63dbc73e')")
    @Sql(statements = "INSERT INTO products (product_id) values (27)")
    @Sql(statements = "INSERT INTO user_carts (cart_id, count_to_buy, product_id, user_id) values (2, 5, 27, '5cfdb965-eb0b-40ed-97be-746c63dbc73e')")
    void changeCountDecrement() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/users/cart/{cart_id}/changeCount", 2)
                .param("option", "decrement"));

        Optional<UsersCart> decremented = cartRepository.findById(2);

        Assertions.assertThat(decremented.get().getCountToBuy()).isEqualTo(4);
    }
    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('367b968c-a0fc-48eb-9693-e35c37bcd5b0')")
    @Sql(statements = "INSERT INTO products (product_id) values (105)")
    void addToCart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/cart/{user_id}/{prod_id}",
                        UUID.fromString("367b968c-a0fc-48eb-9693-e35c37bcd5b0"), 105));

        Optional<UsersCart> byId = cartRepository.findById(1);

        assertTrue(byId.isPresent());
    }
}