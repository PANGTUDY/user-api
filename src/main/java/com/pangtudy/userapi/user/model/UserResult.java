package com.pangtudy.userapi.user.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResult {
    private Long id;
    private String name;
}
