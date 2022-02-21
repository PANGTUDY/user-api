package com.pangtudy.userapi.user.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    void 사용자_목록_조회() throws Exception {
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

    @DisplayName("특정 사용자 조회")
    @Test
    void 특정_사용자_조회() throws Exception {
        // given
        doReturn(userResult()).when(userService).get(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/{email}", "test@test.test")
        );

        // then
        final MvcResult mvcResult = resultActions.andDo(print()).andExpect(status().isOk()).andReturn();
        final String token = mvcResult.getResponse().getContentAsString();
        assertThat(token).isNotNull();
    }

    @DisplayName("특정 사용자 삭제")
    @Test
    void 특정_사용자_삭제() throws Exception {
        // given
        doReturn(userResult()).when(userService).delete(any());

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/{email}", "test@test.test")
        );

        // then
        final MvcResult mvcResult = resultActions.andDo(print()).andExpect(status().isOk()).andReturn();
        final String token = mvcResult.getResponse().getContentAsString();
        assertThat(token).isNotNull();
    }

    private Object userList() {
        final List<UserResult> userList = new ArrayList<>();

        userList.add(new UserResult((long)1, "test@test.test", "test", "pw1", UserRole.ROLE_USER, "1"));
        userList.add(new UserResult((long)2, "test2@test.test", "test2", "pw2", UserRole.ROLE_USER, "2"));

        return userList;
    }

    private Object userResult() {
        final UserResult userResult = new UserResult((long)1, "test@test.test", "test", "pw1", UserRole.ROLE_USER, "1");
        return userResult;
    }

}
