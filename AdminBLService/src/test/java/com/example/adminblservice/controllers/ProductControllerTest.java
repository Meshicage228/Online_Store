package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.entity.product.ProductImage;
import com.example.adminblservice.exceptions.ProductNotFoundException;
import com.example.adminblservice.exceptions.handlers.ProductExceptionHandler;
import com.example.adminblservice.mappers.product.ProductMapper;
import com.example.adminblservice.repository.ImageRepository;
import com.example.adminblservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
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
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    public void setUp() {
        ProductExceptionHandler productExceptionHandler = new ProductExceptionHandler();
    }
    @Test
    @Sql(statements = "INSERT INTO products (product_id, title, price, description, count) VALUES (1, 'Product2', 120, 'good Product', 100)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAllSearchPaginatedSortedProducts() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/sorted/{page}/{size}", 0, 10)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("price", "200")
                        .param("title", "Product2")
                )
                .andReturn();
        Page<ProductDto> page1 = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Page<ProductDto>>(){});
    }

    @Test
    void findProductById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/{id}", 1)).andReturn();

        ProductEntity entity = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductEntity.class);

        Assertions.assertThat(entity).isNotNull();
    }

    @Test
    void findProductByIdFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/products/{id}", 125))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProductNotFoundException));
    }

    @Test
    void update() {
    }

    @Test
    @Sql(statements = "INSERT INTO products (product_id, title, price, description, count) VALUES (10, 'Product10', 120, 'good Product', 100)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/products/{id}/delete", 10)).andReturn();

        Optional<ProductEntity> byId = repository.findById(10);

        assertTrue(byId.isEmpty());
    }

    @Test
    void deleteImage() {
    }

    @Test
    void updatePicture() {
    }

    @Test
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

        Assertions.assertThat(byId.get()).isNotNull();
    }

    @Test
    @Sql(statements = "INSERT INTO products (product_id, title, price, description, count) VALUES (4, 'Product2', 120, 'good Product', 100)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addNewImage() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.APPLICATION_JSON_VALUE,
                "Hello, World!".getBytes()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v1/products/{id}", 4)
                        .file(mockMultipartFile)
                )
                .andReturn();

        Optional<ProductImage> byId = imageRepository.findById(1);

        Assertions.assertThat(byId.get()).isNotNull();

    }
}