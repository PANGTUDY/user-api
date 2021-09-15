package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public Object getUsers() {
        return userService.getAll();
    }

    @PostMapping("")
    public Object addUser(@RequestBody @Valid UserParam param) {
        return userService.add(param);
    }
}
