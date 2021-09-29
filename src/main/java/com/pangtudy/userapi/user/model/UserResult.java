package com.pangtudy.userapi.user.model;

import com.pangtudy.userapi.user.config.UserRole;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResult {
    private Long id;

    private String email;

    private String name;

    private String password;

    private UserRole role;

    private String salt;
}
