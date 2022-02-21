package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import com.pangtudy.userapi.user.util.SaltUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    private SaltUtil saltUtil;

    @DisplayName("회원 가입")
    @Test
    void 회원_가입() {
        // given
        final UserParam userParam = userParam();
        final String salt = saltUtil.genSalt();
        final String encoded = saltUtil.encodePassword(salt, userParam.getPassword());

        // when
        doReturn(new UserEntity((long)1, "test@test.test", "test", encoded, UserRole.ROLE_USER, salt)).when(userRepository).save(any());
        final UserResult user = (UserResult) authService.signUpUser(userParam);

        // then
        assertThat(userParam.getEmail()).isEqualTo(userParam.getEmail());

        // verify
        verify(userRepository, times(1)).save(any());
    }

    private UserParam userParam() {
        final UserParam userParam = new UserParam();
        userParam.setEmail("test@test.test");
        userParam.setPassword("test");
        return userParam;
    }

}
