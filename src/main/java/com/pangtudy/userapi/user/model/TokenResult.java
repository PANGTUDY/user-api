package com.pangtudy.userapi.user.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResult {
    private String accessToken;

    private String refreshToken;
}
