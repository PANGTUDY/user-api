package com.pangtudy.userapi.user.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.service.AuthService;
import com.pangtudy.userapi.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("사용자 목록 조회")
    @Test
    void getUserList() throws Exception {
        // given
        doReturn(userList()).when(userService).getAll();

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/users")
        );

        // then
        final MvcResult mvcResult = resultActions.andDo(print()).andExpect(status().isOk()).andReturn();
        final List<UserResult> response = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), new TypeToken<List<UserResult>>(){}.getType());
        assertThat(response.size()).isEqualTo(2);
    }

    private Object userList() {
        final List<UserResult> userList = new ArrayList<>();

        userList.add(new UserResult((long)1, "test@test.test", "test", "pw1", UserRole.ROLE_USER, "1"));
        userList.add(new UserResult((long)2, "test2@test.test", "test2", "pw2", UserRole.ROLE_USER, "2"));

        return userList;
    }

}
