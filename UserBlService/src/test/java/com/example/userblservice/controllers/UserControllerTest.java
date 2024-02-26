package com.example.userblservice.controllers;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.exceptions.handler.UserExceptionHandler;
import com.example.userblservice.repository.user.CardRepository;
import com.example.userblservice.repository.user.CommentaryRepository;
import com.example.userblservice.repository.user.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentaryRepository commentaryRepository;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    public void setUp() {
        UserExceptionHandler productExceptionHandler = new UserExceptionHandler();
    }

    @Test
    void saveUser() throws Exception {
        UserDto build = UserDto.builder()
                .name("Test")
                .password("123123")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/save")
                .param("name", build.getName())
                .param("password", build.getPassword()))
                .andReturn();

        UserDto productDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        Optional<UserEntity> byId = userRepository.findById(productDto.getId());

        assertTrue(byId.isPresent());
        Assertions.assertThat(byId.get().getName()).isEqualTo(build.getName());
        Assertions.assertThat(byId.get().getPassword()).isNotNull();
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id, name, avatar) values ('eab065b8-5a25-4e40-979c-aa79e7dfe030', 'TestOrder', 25535)")
    void getAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("name", "TestOrder")
                )
                .andReturn();
        LinkedHashMap page1 = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), LinkedHashMap.class);
        Object o = page1.get("content");
        List<Object> objects2 = Arrays.asList(o);
        String o1 = (String) ((LinkedHashMap) ((ArrayList) objects2.get(0)).get(0)).get("id");

        Assertions.assertThat(o1).isEqualTo("eab065b8-5a25-4e40-979c-aa79e7dfe030");
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id, name) values ('552c85fc-a43e-47fc-bb2c-057c2b8928e2','test')")
    void findExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/find")
                .param("find", "test")).andReturn();

        Boolean b = mapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        assertTrue(b);
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('2d5a4adf-9342-478f-85b3-3b46bf9a2302')")
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users/{id}",
                        UUID.fromString("2d5a4adf-9342-478f-85b3-3b46bf9a2302")));

        Optional<UserEntity> byId = userRepository.findById(UUID.fromString("2d5a4adf-9342-478f-85b3-3b46bf9a2302"));

        assertTrue(byId.isEmpty());
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id, password, avatar) values ('aaf5f4bb-1094-3332-a6a3-d2c415425c30', 41212, 5323)")
    void personalUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{id}",
                UUID.fromString("aaf5f4bb-1094-3332-a6a3-d2c415425c30")))
                .andReturn();

        UserDto dto = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertThat(dto.getId()).isEqualTo(UUID.fromString("aaf5f4bb-1094-3332-a6a3-d2c415425c30"));
    }
    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('aaf5f4bb-1094-3332-a6a3-d2c415425c31')")
    @Sql(statements = "INSERT INTO products (product_id) values (52)")
    @Sql(statements = "INSERT INTO users_favorite_products (product_id, user_id) values (52, 'aaf5f4bb-1094-3332-a6a3-d2c415425c31')")
    void removeFavorite() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/users/{user_id}/remove_favorite/{prod_id}",
                UUID.fromString("aaf5f4bb-1094-3332-a6a3-d2c415425c31"), 52
        ));

        Optional<UserEntity> byId = userRepository.findById(UUID.fromString("aaf5f4bb-1094-3332-a6a3-d2c415425c31"));

        assertTrue(byId.isPresent());
        assertTrue(byId.get().getFavoriteProducts().isEmpty());
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('648b1557-0ae3-4f0d-80b6-558ce4025536')")
    @Sql(statements = "INSERT INTO products (product_id) values (100)")
    void leaveCommentary() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/{user_id}/comment/{product_id}",
                UUID.fromString("648b1557-0ae3-4f0d-80b6-558ce4025536"), 100)
                                .param("commentary", "good"));

        Optional<Commentary> byId = commentaryRepository.findById(1);

        assertTrue(byId.isPresent());
    }

    @Test
    @Sql(statements = "INSERT INTO users (user_id) values ('d69a4f7e-bfa9-4624-83b3-69ee6db53b71')")
    void addNewCard() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/users/card/{user_id}",
                UUID.fromString("d69a4f7e-bfa9-4624-83b3-69ee6db53b71")))
                .andReturn();

        UserCard card = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserCard.class);

        assertNotNull(card.getId());
        assertTrue(cardRepository.findById(card.getId()).isPresent());
    }
}