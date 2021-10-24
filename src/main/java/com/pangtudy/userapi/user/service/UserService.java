package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserParam;

public interface UserService {
    Object getAll();

    Object get(String email);

    Object edit(UserParam param);

    Object delete(String email);
}
