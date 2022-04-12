package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import javassist.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Object signUpUser(UserParam param);

    Object loginUser(UserParam param, HttpServletResponse res);

    boolean logoutUser(HttpServletRequest req);

    void sendVerificationMail(UserParam param) throws NotFoundException;

    void verifyEmail(String key) throws NotFoundException;

    void modifyUserRole(UserEntity user, UserRole userRole);

    void sendPasswordChangeMail(UserParam param) throws NotFoundException;

    void modifyUserPassword(UserEntity user, String password);

    Object tokenRefresh(HttpServletRequest req, HttpServletResponse res);
}
