package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserRequestDto;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

    Object signUp(UserRequestDto userRequestDto);

    Object login(UserRequestDto userRequestDto);

    Object logout(HttpServletRequest req);

    Object refresh(HttpServletRequest req);

    Object sendVerificationMail(UserRequestDto userRequestDto);

    Object verifyEmail(String key);

    Object sendPasswordChangeMail(UserRequestDto userRequestDto);

}
