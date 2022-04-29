package com.pangtudy.userapi.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenResponseDto {

    private String accessToken;

    private String refreshToken;

}
