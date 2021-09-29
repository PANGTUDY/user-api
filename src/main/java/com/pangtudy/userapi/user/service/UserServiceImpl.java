package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public Object getAll() {
        List<UserEntity> users = userRepository.findAll();

        if (Optional.ofNullable(users).isPresent()) {
            return users.stream()
                    .map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResult()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private <R, T> T sourceToDestinationTypeCasting(R source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
