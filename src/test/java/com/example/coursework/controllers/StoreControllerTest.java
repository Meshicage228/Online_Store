package com.example.coursework.controllers;

import com.example.coursework.clients.OrderClient;
import com.example.coursework.clients.ProductClient;
import com.example.coursework.clients.UsersClient;
import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.dto.user.AuthorizeDao;
import com.example.coursework.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = StoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class StoreControllerTest {
    @MockBean
    private ProductClient productClient;

    @MockBean
    private UsersClient usersClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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
    void getPage() throws Exception {
        List<ProductDto> products = List.of(new ProductDto(), new ProductDto(), new ProductDto(), new ProductDto(), new ProductDto());
        Page<ProductDto> productPage = new PageImpl<>(products);

        Mockito.when(productClient.getAllSearchPaginatedSortedProducts(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(productPage);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/store/{page}/{size}", 0, 10))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        var productDtos = (Page<ProductDto>)modelAndView.getModelMap().get("totalPage");

        Assertions.assertThat(viewName).isEqualTo("storePage");
        Assertions.assertThat(productDtos).hasSize(products.size());
        Assertions.assertThat(productPage.getTotalPages()).isEqualTo(1);
    }

    @Test
    void getPersonalPage() throws Exception {
        ProductDto build = ProductDto.builder()
                .id(1)
                .build();

        Mockito.when(productClient.findProductById(Mockito.any()))
                .thenReturn(build);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/store/catalog/{prod_id}", 10))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        var product = (ProductDto)modelAndView.getModelMap().get("product");

        Assertions.assertThat(viewName).isEqualTo("personalProductPage");
        Assertions.assertThat(product.getId()).isEqualTo(build.getId());
    }
    @Test
    void authorizeSuccess() throws Exception {
        AuthorizeDao build = AuthorizeDao.builder()
                .nameAuth("TestAuth")
                .password("123123123")
                .doublePass("123123123")
                .build();

        Mockito.when(usersClient.findExists(Mockito.any()))
                .thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/store/authorize")
                        .flashAttr("enterUser", build))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        Assertions.assertThat(viewName).isEqualTo("redirect:/store/login");
    }

}