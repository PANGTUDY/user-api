package com.pangtudy.userapi.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Builder
public class UserParam {
    private Long id;

    @NotEmpty
    private String name;
}
