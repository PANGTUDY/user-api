package com.pangtudy.userapi.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserRequestDto {

    public interface GlobalValidationGroup {}

    public interface SignUpValidationGroup extends GlobalValidationGroup {}

    public interface LoginValidationGroup extends GlobalValidationGroup {}

    public interface VerifyValidationGroup extends GlobalValidationGroup {}

    @NotBlank(groups = {SignUpValidationGroup.class, LoginValidationGroup.class, VerifyValidationGroup.class}, message = "email 필드는 반드시 값이 존재하고 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    @Email(groups = GlobalValidationGroup.class)
    private String email;

    @NotBlank(groups = SignUpValidationGroup.class, message = "name 필드는 반드시 값이 존재하고 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    private String name;

    @NotBlank(groups = {SignUpValidationGroup.class, LoginValidationGroup.class}, message = "password 필드는 반드시 값이 존재하고 공백 문자를 제외한 길이가 0보다 커야 합니다.")
    private String password;

}
