package com.pangtudy.userapi.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDto {

    private String status;

    private String message;

    private Object data;

}
