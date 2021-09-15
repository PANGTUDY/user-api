package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public Object getAll() {
        List<UserEntity> users = userRepository.findAll();

        if (Optional.ofNullable(users).isPresent()) {
            return users.stream()
                    .map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResult()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Transactional
    public Object add(UserParam param) {
        UserEntity user = userRepository.save(sourceToDestinationTypeCasting(param, new UserEntity()));
        return sourceToDestinationTypeCasting(user, new UserResult());
    }

    private <R, T> T sourceToDestinationTypeCasting(R source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
