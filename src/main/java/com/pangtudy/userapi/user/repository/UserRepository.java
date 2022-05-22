package com.pangtudy.userapi.user.repository;

import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.role = :role WHERE u.id IN (:ids)")
    int updateRoleSelectedUsers(@Param("role") UserRole role, @Param("ids") List<Long> ids);

}
