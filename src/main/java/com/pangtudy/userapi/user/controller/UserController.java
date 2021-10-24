package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{email}")
    public Object getUserOne(@PathVariable("email") String email) {
        return userService.get(email);
    }

    @PutMapping("/{email}")
    public Object edit(@RequestBody UserParam param, @PathVariable("email") String email) {
        param.setEmail(email);
        return userService.edit(param);
    }

    @DeleteMapping("/{email}")
    public Object delete(@PathVariable("email") String email) {
        return userService.delete(email);
    }

}
