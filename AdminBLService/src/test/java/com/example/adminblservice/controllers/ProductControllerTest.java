package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.entity.product.ProductImage;
import com.example.adminblservice.exceptions.ProductNotFoundException;
import com.example.adminblservice.exceptions.handlers.ProductExceptionHandler;
import com.example.adminblservice.repository.ImageRepository;
import com.example.adminblservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ImageRepository imageRepository;

    private final String PRODUCT_TITLE = "Producttest";

    @BeforeAll
    public static void setUp() {
        ProductExceptionHandler productExceptionHandler = new ProductExceptionHandler();
    }
    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void getAllSearchPaginatedSortedProducts() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/products/sorted/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("price", "200")
                        .param("title", PRODUCT_TITLE)
                )
                .andReturn();
        LinkedHashMap page1 = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), LinkedHashMap.class);
        Object o = page1.get("content");
        List<Object> objects2 = Arrays.asList(o);
        String o1 = (String) ((LinkedHashMap) ((ArrayList) objects2.get(0)).get(0)).get("title");

        Assertions.assertThat(o1).isEqualTo(PRODUCT_TITLE);
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void findProductById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/products/{id}", 1)).andReturn();

        ProductEntity entity = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductEntity.class);

        Assertions.assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void findProductByIdFail() throws Exception {
        mockMvc.perform(get("/v1/products/{id}", 125))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProductNotFoundException));
    }

    @Test
    @Transactional
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void update() throws Exception {
        ProductDto build = ProductDto.builder()
                .title("ProductNew")
                .price(1000f)
                .description("new")
                .count(200)
                .build();

        MvcResult mvcResult = mockMvc.perform(put("/v1/products/{id}/change", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(build))
        ).andReturn();

        ProductDto productDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);


        assertThat(productDto)
                .extracting("id", "title", "price", "description", "count")
                .isNotNull()
                .containsExactly(1, build.getTitle(), build.getPrice(), build.getDescription(), build.getCount());
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/v1/products/{id}/delete", 1));

        Optional<ProductEntity> byId = repository.findById(17);

        assertTrue(byId.isEmpty());
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void deleteImage() throws Exception {
        mockMvc.perform(delete("/v1/products/image/{image_id}/delete", 1));

        Optional<ProductImage> byId = imageRepository.findById(1);

        assertTrue(byId.isEmpty());
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void updatePicture() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "fileName",
                "hello.txt",
                MediaType.APPLICATION_JSON_VALUE,
                "Hello, Worlds!".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products/image/{image_id}/update", 1)
                .file("file", mockMultipartFile.getBytes()));

        Optional<ProductImage> byId = imageRepository.findById(1);

        assertTrue(byId.isPresent());
        Assertions.assertThat(byId.get().getImage()).isEqualTo(mockMultipartFile.getBytes());
    }

    @Test
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void saveProduct() throws Exception {
        ProductDto build = ProductDto.builder()
                .title("Product2")
                .price(123f)
                .count(1000)
                .description("GoodProduct2")
                .imagesToThrow(List.of(new MockMultipartFile(
                        "file",
                        "hello.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes()).getBytes()))
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/v1/products/save")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(build))
                )
                .andReturn();

        ProductDto productDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDto.class);

        Optional<ProductEntity> byId = repository.findById(productDto.getId());

        assertTrue(byId.isPresent());
    }

    @Test
    @Sql(value = "classpath:/data/insertData.sql", executionPhase = BEFORE_TEST_METHOD)
    @Sql(value = "classpath:/data/cleanUpAll.sql", executionPhase = AFTER_TEST_METHOD)
    void addNewImage() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.APPLICATION_JSON_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products/{id}", 1)
                .file(mockMultipartFile)
        );

        Optional<ProductImage> byId = imageRepository.findById(38);

        assertTrue(byId.isPresent());

    }
}