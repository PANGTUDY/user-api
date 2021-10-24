package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @DisplayName("사용자 목록 조회")
    @Test
    void 사용자_목록_조회() {
        // given
        doReturn(userList()).when(userRepository).findAll();

        // when
        final List<UserResult> userList = (List<UserResult>) userService.getAll();

        // then
        assertThat(userList.size()).isEqualTo(2);
    }

    private Object userList() {
        final List<UserEntity> userList = new ArrayList<>();

        userList.add(new UserEntity((long)1, "test@test.test", "test", "pw1", UserRole.ROLE_USER, "1"));
        userList.add(new UserEntity((long)2, "test2@test.test", "test2", "pw2", UserRole.ROLE_USER, "2"));

        return userList;
    }

}
