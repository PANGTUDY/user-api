package com.pangtudy.userapi.user.model;

import com.pangtudy.userapi.user.model.UserRole;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "email")
    private String email;

    @Setter
    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "password")
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.ROLE_NOT_PERMITTED;

    @Setter
    @Column(name = "salt")
    private String salt;

}
