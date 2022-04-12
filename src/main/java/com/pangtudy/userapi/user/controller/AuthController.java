package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/logout")
    public Object logout(HttpServletRequest req, HttpServletResponse res) {
        String response = "로그아웃 완료.";
        if (authService.logoutUser(req) == false) {
            res.setStatus(403);
            response = "로그아웃을 수행하는데 문제가 발생했습니다.";
        }
        return response;
    }

    @GetMapping("/me")
    public Object me(@AuthenticationPrincipal UserDetails user) {
        return user.getUsername();
    }

    @PostMapping("/verify")
    public Object verify(@RequestBody UserParam param) {
        String response = "성공적으로 인증메일을 보냈습니다.";
        try {
            authService.sendVerificationMail(param);
        } catch (Exception exception) {
            response = "인증메일을 보내는데 문제가 발생했습니다.";
        }
        return response;
    }

    @GetMapping("/verify/{key}")
    public Object getVerify(@PathVariable String key) {
        String response = "성공적으로 인증메일을 확인했습니다.";
        try {
            authService.verifyEmail(key);
        } catch (Exception e) {
            response = "인증메일을 확인하는데 실패했습니다.";
        }
        return response;
    }

    @PostMapping("/pwinquiry")
    public Object pwinquiry(@RequestBody UserParam param) {
        String response = "이메일로 임시 비밀번호를 발송하였습니다.";
        try {
            authService.sendPasswordChangeMail(param);
        } catch (Exception exception) {
            response = "임시 비밀번호를 발송하는데 문제가 발생했습니다.";
        }
        return response;
    }
	
	@PostMapping("/token")
    public Object token(HttpServletRequest req, HttpServletResponse res) {
        Object result = authService.tokenRefresh(req, res);
		if (result == null) {
            res.setStatus(403);
			return "토큰을 갱신하는데 문제가 발생했습니다.";
		}
        return result;
    }

}
