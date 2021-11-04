package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Override
    public Object getAll() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResult()))
                .collect(Collectors.toList());
    }

    @Override
    public Object get(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (Optional.ofNullable(user).isPresent()) {
            return user.map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResult()));
        }
        return null;
    }

    @Override
    @Transactional
    public Object edit(UserParam param) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(param.getEmail());
        if (Optional.ofNullable(userEntity).isPresent()) {
            UserEntity user = userEntity.get();
            copyNonNullProperties(sourceToDestinationTypeCasting(param, new UserEntity()), user);
            return sourceToDestinationTypeCasting(userRepository.save(user), new UserResult());
        }
        return null;
    }

    @Override
    @Transactional
    public Object delete(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (Optional.ofNullable(user).isPresent()) {
            userRepository.deleteByEmail(email);
            return user.map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResult()));
        }
        return null;
    }

    private <R, T> T sourceToDestinationTypeCasting(R source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    private void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
