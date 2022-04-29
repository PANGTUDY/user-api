package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserRequestDto;

public interface UserService {

    Object getUsers();

    Object retrieveUser(Long id);

    Object updateUser(UserRequestDto userRequestDto, Long id);

    Object deleteUser(Long id);

}
