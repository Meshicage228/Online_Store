package com.example.orderblservice.controller;

import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.exceptions.OutOfStockException;
import com.example.orderblservice.exceptions.handler.OrderExceptionHandler;
import com.example.orderblservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        OrderExceptionHandler productExceptionHandler = new OrderExceptionHandler();
    }
    @Test
    @Sql(statements = "INSERT INTO products (product_id, title) values (43, 'Product1')",
         executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO purchases (id, status, product_product_id) values (2, 'DONE', 43)",
         executionPhase = BEFORE_TEST_METHOD)
    void getOrders() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("status", "DONE")
                        .param("title", "Product1")
                )
                .andReturn();
        LinkedHashMap page1 = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), LinkedHashMap.class);
        Object o = page1.get("content");
        List<Object> objects2 = Arrays.asList(o);
        Integer o1 = (Integer) ((LinkedHashMap) ((ArrayList) objects2.get(0)).get(0)).get("id");

        Assertions.assertThat(o1).isEqualTo(2);
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('13449768-9791-440e-8cd6-81ac50f991b3')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO products (product_id, title, count, price) values (13, 'Product2', 100, 20)",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO users_cards (id, money, user_user_id) values (2, 1000, '13449768-9791-440e-8cd6-81ac50f991b3')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO user_carts (cart_id, count_to_buy, product_id, user_id) values (2, 20, 13, '13449768-9791-440e-8cd6-81ac50f991b3')",
            executionPhase = BEFORE_TEST_METHOD)
    void createPurchase() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/create/{user_id}",
                        UUID.fromString("13449768-9791-440e-8cd6-81ac50f991b3")));

        Optional<Orders> byId = orderRepository.findById(1);

        assertTrue(byId.isPresent());
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('126266be-790a-412b-bdec-7143a19fbde8')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO products (product_id, title, count, price) values (14, 'Product3', 100, 20)",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO users_cards (id, money, user_user_id) values (3, 1000, '126266be-790a-412b-bdec-7143a19fbde8')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO user_carts (cart_id, count_to_buy, product_id, user_id) values (3, 120, 14, '126266be-790a-412b-bdec-7143a19fbde8')",
            executionPhase = BEFORE_TEST_METHOD)
    void catchExceptionOnCreationOrder() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/create/{user_id}",
                        UUID.fromString("126266be-790a-412b-bdec-7143a19fbde8")))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof OutOfStockException));
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('401b7495-337d-4fa1-b4fb-bdb2a4ed0c2f')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO products (product_id, title, count, price) values (15, 'Product10', 100, 20)",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO users_cards (id, money, user_user_id) values (4, 1000, '401b7495-337d-4fa1-b4fb-bdb2a4ed0c2f')",
            executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO user_carts (cart_id, count_to_buy, product_id, user_id) values (4, 80, 15, '401b7495-337d-4fa1-b4fb-bdb2a4ed0c2f')",
            executionPhase = BEFORE_TEST_METHOD)
    void notEnoughMoney() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/create/{user_id}",
                        UUID.fromString("401b7495-337d-4fa1-b4fb-bdb2a4ed0c2f")))
                .andReturn();

        Boolean b = mapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        Assertions.assertThat(b).isFalse();
    }
}