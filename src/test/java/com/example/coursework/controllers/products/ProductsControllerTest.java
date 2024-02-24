package com.example.coursework.controllers.products;

import com.example.coursework.clients.ProductClient;
import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.services.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(value = ProductsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProductsControllerTest {
    @MockBean
    private ProductClient client;

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
    void getById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/admin/products/{id}", 1))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        Mockito.verify(client, Mockito.times(1)).findProductById(Mockito.any());
        Assertions.assertThat(viewName).isEqualTo("adminUpdateProduct");
    }

    @Test
    void updateSuccess() throws Exception {
        //GIVEN
        ProductDto build = ProductDto.builder()
                .title("newTitle")
                .count(1000)
                .fileImage(List.of(new MockMultipartFile(
                        "file",
                        "hello.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes())))
                .description("Good Product")
                .price(5000f)
                .build();

        //WHEN
        Mockito.when(client.update(Mockito.any(), Mockito.any())).thenReturn(build);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/admin/products/{id}/change", 2)
                        .flashAttr("modelToUpdate", build)
                )
                .andDo(print())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();
        var productDto = (ProductDto) modelAndView.getModelMap().get("modelToUpdate");

        //THEN

        Assertions.assertThat(viewName).isEqualTo("redirect:/admin/products/2");
        Assertions.assertThat(productDto).isEqualTo(build);
    }

    @Test
    void updateFailed() throws Exception {
        //GIVEN
        ProductDto build = ProductDto.builder()
                .title("newTitle")
                .count(-1)
                .description("")
                .price(0f)
                .build();

        ProductDto fromBd = ProductDto.builder()
                .title("newTitle")
                .count(1000)
                .description("Good Product")
                .price(5000f)
                .build();

        //WHEN
        Mockito.when(client.findProductById(Mockito.any())).thenReturn(fromBd);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/admin/products/{id}/change", 2)
                        .flashAttr("modelToUpdate", build)
                )
                .andDo(print())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();
        var productDto = (ProductDto) modelAndView.getModelMap().get("modelToUpdate");

        //THEN

        Assertions.assertThat(viewName).isEqualTo("adminUpdateProduct");
        Assertions.assertThat(productDto).isEqualTo(fromBd);
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/admin/products/{id}/delete", 1));

        Mockito.verify(client, Mockito.times(1)).deleteProduct(Mockito.any());
    }

    @Test
    void deleteImage() throws Exception {
        mockMvc.perform(delete("/admin/products/{product_id}/image/{image_id}/delete", 1, 1));

        Mockito.verify(client, Mockito.times(1)).deleteImage(Mockito.any());
    }
    @Test
    void saveProductFailure() throws Exception {
        //GIVEN
        ProductDto build = ProductDto.builder()
                .title("newTitle")
                .count(1000)
                .description("good")
                .price(5000f)
                .build();
        //WHEN
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/admin/products/save")
                        .flashAttr("modelToSave", build)
                )
                .andDo(print())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        var o = (ProductDto) modelAndView.getModelMap().get("modelToSave");
        String viewName = modelAndView.getViewName();
        //THEN

        Assertions.assertThat(o).isEqualTo(build);
        Assertions.assertThat(viewName).isEqualTo("createProductAdmin");
    }
    @Test
    void saveProductSuccess() throws Exception {
        //GIVEN
        ProductDto build = ProductDto.builder()
                .title("newTitle")
                .count(1000)
                .description("Good Product")
                .fileImage(List.of(new MockMultipartFile(
                        "file",
                        "hello.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello, World!".getBytes())))
                .price(5000f)
                .build();
        //WHEN
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/admin/products/save")
                        .flashAttr("modelToSave", build)
                )
                .andDo(print())
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();
        var o = (ProductDto) modelAndView.getModelMap().get("modelToSave");
        //THEN

        Assertions.assertThat(o).isEqualTo(new ProductDto());
        Assertions.assertThat(viewName).isEqualTo("createProductAdmin");
    }
}