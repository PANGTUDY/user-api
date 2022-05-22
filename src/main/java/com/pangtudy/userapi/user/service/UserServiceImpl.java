package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.exception.BadRequestException;
import com.pangtudy.userapi.user.exception.NotFoundException;
import com.pangtudy.userapi.user.model.*;
import com.pangtudy.userapi.user.repository.UserRepository;
import com.pangtudy.userapi.user.util.SaltUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final SaltUtil saltUtil;

    @Override
    public Object getUsers() {
        List<UserEntity> userEntityList = userRepository.findAll();
        List<UserResponseDto> userResponseDtoList = userEntityList.stream()
                .map(userEntity -> sourceToDestinationTypeCasting(userEntity, new UserResponseDto()))
                .collect(Collectors.toList());
        return new ResponseDto("success", "ok", userResponseDtoList);
    }

    @Override
    public Object retrieveUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 ID의 유저가 존재하지 않습니다."));
        UserResponseDto userResponseDto = sourceToDestinationTypeCasting(userEntity, new UserResponseDto());
        return new ResponseDto("success", "ok", userResponseDto);
    }

    @Override
    @Transactional
    public Object updateUser(UserRequestDto userRequestDto, Long id) {
        UserEntity userEntityOld = userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 ID의 유저가 존재하지 않습니다."));
        UserEntity userEntityNew = sourceToDestinationTypeCasting(userRequestDto, new UserEntity());

        String password = userRequestDto.getPassword();
        if (password != null) {
            String salt = saltUtil.genSalt();
            userEntityNew.setSalt(salt);
            userEntityNew.setPassword(saltUtil.encodePassword(salt, password));
        }

        copyNonNullProperties(userEntityNew, userEntityOld);
        userRepository.save(userEntityOld);

        return new ResponseDto("success", "ok", null);
    }

    @Override
    @Transactional
    public Object deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 ID의 유저가 존재하지 않습니다."));
        userRepository.deleteById(id);
        return new ResponseDto("success", "ok", null);
    }

    @Override
    @Transactional
    public Object updateRoles(UpdateRolesRequestDto updateRolesRequestDto) {
        List<Long> ids = updateRolesRequestDto.getIds();
        String role = updateRolesRequestDto.getRole();

        if (role.equals("user")) {
            userRepository.updateRoleSelectedUsers(UserRole.ROLE_USER, ids);
        }
        else if (role.equals("admin")) {
            userRepository.updateRoleSelectedUsers(UserRole.ROLE_ADMIN, ids);
        }
        else {
            throw new BadRequestException("role 필드의 값은 user 또는 admin 이어야 합니다.");
        }

        return new ResponseDto("success", "ok", null);
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
