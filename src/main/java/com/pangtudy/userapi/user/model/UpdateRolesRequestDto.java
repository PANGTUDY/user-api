package com.pangtudy.userapi.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class UpdateRolesRequestDto {

    @NotNull
    private List<Long> ids;

    @NotBlank
    private String role;

}
