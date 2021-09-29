package com.pangtudy.userapi.user.model;

import com.pangtudy.userapi.user.config.UserRole;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParam {
    private Long id;

    @NotEmpty
    private String email;

    private String name;

    private String password;

    private UserRole role;

    private String salt;
}
