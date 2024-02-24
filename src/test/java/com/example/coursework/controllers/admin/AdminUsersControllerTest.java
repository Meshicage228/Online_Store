package com.example.coursework.controllers.admin;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.configuration.token.TokenGenerationFilter;
import com.example.coursework.configuration.token.TokenValidationFilter;
import com.example.coursework.dto.user.UserDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(value = AdminUsersController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUsersControllerTest {

    @MockBean
    private UsersClient client;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService service;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setUp(){
       TokenGenerationFilter tokenGenerationFilter = new TokenGenerationFilter(service, encoder);
       TokenValidationFilter validationFilter = new TokenValidationFilter(service);
    }

    @Test
    void getPage() throws Exception {
        // GIVEN
        List<UserDto> userDtos = List.of(new UserDto(), new UserDto(), new UserDto());
        Page<UserDto> page = new PageImpl<>(userDtos);

        //WHEN
        Mockito.when(client.getAllUsers(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/admin/users/{page}/{size}", 0, 10))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        String viewName = modelAndView.getViewName();

        var users = (Page<UserDto>)modelAndView.getModelMap().get("totalPage");

        //RESULT
        Assertions.assertThat(viewName).isEqualTo("adminUsersPage");
        Assertions.assertThat(users.getContent()).hasSize(userDtos.size());
        Assertions.assertThat(users.getTotalPages()).isEqualTo(1);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", UUID.randomUUID()));

        Mockito.verify(client, Mockito.times(1)).deleteUser(Mockito.any());
    }
    @Test
    void deleteCommentary() throws Exception {
        mockMvc.perform(post("/admin/users/deleteComment/{comment_id}/{prod_id}", 1, 1));

        Mockito.verify(client, Mockito.times(1)).deleteComm(Mockito.any());
    }
}