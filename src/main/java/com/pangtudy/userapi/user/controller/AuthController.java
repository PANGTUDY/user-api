package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Object signUpUser(@RequestBody UserParam param) {
        return authService.signUpUser(param);
    }

    @PostMapping("/login")
    public Object login(@RequestBody UserParam param, HttpServletResponse res) {
        return authService.loginUser(param, res);
    }

}
