package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByEmail(username);

        if (Optional.ofNullable(userEntity).isEmpty()) {
            throw new UsernameNotFoundException(username + " : 사용자 존재하지 않음");
        }

        UserEntity user = userEntity.get();

        return new User(
                user.getEmail(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole().toString())
        );
    }
}
