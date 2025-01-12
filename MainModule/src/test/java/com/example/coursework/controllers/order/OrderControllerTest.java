package com.example.coursework.controllers.order;

import com.example.coursework.clients.OrderClient;
import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import com.example.coursework.dto.product.OrderDto;
import com.example.coursework.services.TokenService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@WebMvcTest(value = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class OrderControllerTest {
    @MockBean
    private OrderClient client;

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
    void getOrders() throws Exception {
        List<OrderDto> orderDtos = List.of(new OrderDto(), new OrderDto(), new OrderDto(), new OrderDto(), new OrderDto());
        Page<OrderDto> orders = new PageImpl<>(orderDtos);

        Mockito.when(client.getOrders(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(orders);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/admin/orders/{page}/{size}", 0, 3))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        var ordersDto = (Page<OrderDto>)modelAndView.getModelMap().get("orderContent");

        Assertions.assertThat(viewName).isEqualTo("adminOrderPage");
        Assertions.assertThat(ordersDto).hasSize(orderDtos.size());
        Assertions.assertThat(orders.getTotalPages()).isEqualTo(1);
    }
}