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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    private final String USER_ID = "aaf5f4bb-1094-3332-a6a3-d2c415425c30";

    @BeforeAll
    public static void setUp() {
        UserExceptionHandler productExceptionHandler = new UserExceptionHandler();
    }

    @Test
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void saveUser() throws Exception {
        UserDto build = UserDto.builder()
                .name("Test")
                .password("123123")
                .build();

        MvcResult mvcResult = mockMvc.perform(post("/v1/users/save")
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
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void getAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/users/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("name", "Testuser")
                )
                .andReturn();
        LinkedHashMap page1 = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), LinkedHashMap.class);
        Object o = page1.get("content");
        List<Object> objects2 = Arrays.asList(o);
        String o1 = (String) ((LinkedHashMap) ((ArrayList) objects2.get(0)).get(0)).get("id");

        Assertions.assertThat(o1).isEqualTo(USER_ID);
    }

    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void findExists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/v1/users/find")
                .param("find", "Testuser")).andReturn();

        Boolean b = mapper.readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        assertTrue(b);
    }

    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/v1/users/{id}",
                        UUID.fromString(USER_ID)));

        Optional<UserEntity> byId = userRepository.findById(UUID.fromString(USER_ID));

        assertTrue(byId.isEmpty());
    }

    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void personalUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/{id}",
                UUID.fromString(USER_ID)))
                .andReturn();

        UserDto dto = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertThat(dto.getId()).isEqualTo(UUID.fromString(USER_ID));
    }
    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void removeFavorite() throws Exception {
        mockMvc.perform(delete("/v1/users/{user_id}/remove_favorite/{prod_id}",
                UUID.fromString(USER_ID), 1
        ));

        Optional<UserEntity> byId = userRepository.findById(UUID.fromString(USER_ID));

        assertTrue(byId.isPresent());
        assertTrue(byId.get().getFavoriteProducts().isEmpty());
    }

    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void leaveCommentary() throws Exception {
        mockMvc.perform(post("/v1/users/{user_id}/comment/{product_id}",
                UUID.fromString(USER_ID), 1)
                                .param("commentary", "good"));

        Optional<Commentary> byId = commentaryRepository.findById(12);

        assertTrue(byId.isPresent());
    }

    @Test
    @Sql(value = "classpath:/data/user/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/user/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void addNewCard() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/v1/users/card/{user_id}",
                UUID.fromString(USER_ID)))
                .andReturn();

        UserCard card = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserCard.class);

        assertNotNull(card.getId());
        assertTrue(cardRepository.findById(card.getId()).isPresent());
    }
}