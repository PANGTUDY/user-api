package com.pangtudy.userapi.user.controller;

import com.pangtudy.userapi.user.model.*;
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
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public Object retrieveUser(@PathVariable("id") Long id) {
        return userService.retrieveUser(id);
    }

    @PutMapping("/{id}")
    public Object updateUser(@RequestBody UserRequestDto userRequestDto, @PathVariable("id") Long id) {
        return userService.updateUser(userRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public Object deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/update-roles")
    public Object updateRoles(@RequestBody @Validated UpdateRolesRequestDto updateRolesRequestDto) {
        return userService.updateRoles(updateRolesRequestDto);
    }

}
