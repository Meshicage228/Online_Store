package com.example.orderblservice.controller;

import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.exceptions.OutOfStockException;
import com.example.orderblservice.exceptions.handler.OrderExceptionHandler;
import com.example.orderblservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    private final String USER_ID = "13449768-9791-440e-8cd6-81ac50f991b3";

    @BeforeAll
    public static void setUp() {
        OrderExceptionHandler productExceptionHandler = new OrderExceptionHandler();
    }
    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void getOrders() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/orders/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("status", "DONE")
                        .param("title", "ProductTest")
                )
                .andReturn();
        LinkedHashMap page1 = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), LinkedHashMap.class);
        Object o = page1.get("content");
        List<Object> objects2 = Arrays.asList(o);
        Integer o1 = (Integer) ((LinkedHashMap) ((ArrayList) objects2.get(0)).get(0)).get("id");

        Assertions.assertThat(o1).isEqualTo(10);
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void createPurchase() throws Exception {
        mockMvc.perform(post("/v1/orders/create/{user_id}",
                        UUID.fromString(USER_ID)));

        Optional<Orders> byId = orderRepository.findById(1);

        assertTrue(byId.isPresent());
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "UPDATE user_carts SET count_to_buy = 120 WHERE cart_id = 1")
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void catchExceptionOnCreationOrder() throws Exception {

        mockMvc.perform(post("/v1/orders/create/{user_id}",
                        UUID.fromString(USER_ID)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof OutOfStockException));
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(statements = "UPDATE users_cards SET money = 0 WHERE id = 1")
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void notEnoughMoney() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/v1/orders/create/{user_id}",
                        UUID.fromString(USER_ID)))
                .andReturn();

        Boolean b = mapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        Assertions.assertThat(b).isFalse();
    }
}