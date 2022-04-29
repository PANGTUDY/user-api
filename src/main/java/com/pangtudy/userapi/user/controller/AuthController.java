package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.UserRequestDto;
import com.pangtudy.userapi.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Object signUp(@RequestBody @Validated(UserRequestDto.SignUpValidationGroup.class) UserRequestDto userRequestDto) {
        return authService.signUp(userRequestDto);
    }

    @PostMapping("/login")
    public Object login(@RequestBody @Validated(UserRequestDto.LoginValidationGroup.class) UserRequestDto userRequestDto) {
        return authService.login(userRequestDto);
    }

    @PostMapping("/logout")
    public Object logout(HttpServletRequest req) {
        return authService.logout(req);
    }

    @PostMapping("/refresh")
    public Object refresh(HttpServletRequest req) {
        return authService.refresh(req);
    }

    @PostMapping("/verify")
    public Object verify(@RequestBody @Validated(UserRequestDto.VerifyValidationGroup.class) UserRequestDto userRequestDto) {
        return authService.sendVerificationMail(userRequestDto);
    }

    @GetMapping("/verify/{key}")
    public Object getVerify(@PathVariable String key) {
        return authService.verifyEmail(key);
    }

    @PostMapping("/pwinquiry")
    public Object pwinquiry(@RequestBody @Validated(UserRequestDto.VerifyValidationGroup.class) UserRequestDto userRequestDto) {
        return authService.sendPasswordChangeMail(userRequestDto);
    }

}
