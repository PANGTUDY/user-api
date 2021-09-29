package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserParam;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    Object signUpUser(UserParam param);

    Object loginUser(UserParam param, HttpServletResponse res);
}
