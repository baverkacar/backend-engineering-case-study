package com.dreamgames.backendengineeringcasestudy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dreamgames.backendengineeringcasestudy.model.user.CreateUserRequest;
import com.dreamgames.backendengineeringcasestudy.model.user.UserProgressResponse;
import com.dreamgames.backendengineeringcasestudy.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureWebClient
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    private final String CONTENT_TYPE = "application/json";
    private final String URL = "/users";


    @Test
    public void shouldCreateUser() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("testUser")
                .email("test-user@gmail.com")
                .password("Testuser123-")
                .build();
        UserProgressResponse userProgressResponse = UserProgressResponse.builder()
                .id(1L)
                .level(1)
                .coins(5000)
                .country("FRANCE")
                .build();
        // when
        when(userService.createUser(createUserRequest)).thenReturn(userProgressResponse);
        ResultActions actions = mockMvc.perform(post(URL + "/create")
                .contentType(CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(createUserRequest))
        );

        // then
        ArgumentCaptor<CreateUserRequest> captor = ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(userService, Mockito.times(1)).createUser(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("testUser");
        assertThat(captor.getValue().getEmail()).isEqualTo("test-user@gmail.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("Testuser123-");

        actions.andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(1),
                jsonPath("$.level").value(1),
                jsonPath("$.coins").value(500),
                jsonPath("$.country").value("FRANCE")
        );
    }
}
