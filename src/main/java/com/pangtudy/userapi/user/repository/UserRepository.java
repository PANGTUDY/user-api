package com.pangtudy.userapi.user.repository;

import com.pangtudy.userapi.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    void deleteByEmail(String email);
}
