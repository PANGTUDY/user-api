package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import javassist.NotFoundException;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Object signUpUser(UserParam param);

    Object loginUser(UserParam param, HttpServletResponse res);

    void sendVerificationMail(UserParam param) throws NotFoundException;

    void verifyEmail(String key) throws NotFoundException;

    void modifyUserRole(UserEntity user, UserRole userRole);
}
