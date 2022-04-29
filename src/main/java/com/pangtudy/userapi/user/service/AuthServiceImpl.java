package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.model.UserRole;
import com.pangtudy.userapi.user.exception.ConflictException;
import com.pangtudy.userapi.user.exception.NotFoundException;
import com.pangtudy.userapi.user.exception.UnauthorizedException;
import com.pangtudy.userapi.user.model.*;
import com.pangtudy.userapi.user.repository.UserRepository;
import com.pangtudy.userapi.user.util.EmailUtil;
import com.pangtudy.userapi.user.util.JwtUtil;
import com.pangtudy.userapi.user.util.RedisUtil;
import com.pangtudy.userapi.user.util.SaltUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final EmailUtil emailUtil;
    private final SaltUtil saltUtil;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    @Transactional
    public Object signUp(UserRequestDto userRequestDto) {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new ConflictException("해당 이메일로 가입한 계정이 이미 존재합니다.");
        }

        String password = userRequestDto.getPassword();
        String salt = saltUtil.genSalt();

        UserEntity userEntity = sourceToDestinationTypeCasting(userRequestDto, new UserEntity());
        userEntity.setSalt(salt);
        userEntity.setRole(UserRole.ROLE_USER);
        userEntity.setPassword(saltUtil.encodePassword(salt, password));
        userRepository.save(userEntity);

        return new ResponseDto("success", "ok", null);
    }

    @Override
    public Object login(UserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(() -> new UnauthorizedException("해당 이메일로 가입한 계정이 존재하지 않습니다."));
        String salt = userEntity.getSalt();
        String password = saltUtil.encodePassword(salt, userRequestDto.getPassword());

        if (!userEntity.getPassword().equals(password)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(userEntity.getId());
        String refreshToken = jwtUtil.generateRefreshToken();

        redisUtil.setDataExpire(refreshToken, userEntity.getId().toString(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new ResponseDto("success", "ok", tokenResponseDto);
    }

    @Override
    public Object logout(HttpServletRequest req) {
        if (req.getHeader("Authorization") == null || !req.getHeader("Authorization").startsWith("Bearer")) {
            throw new UnauthorizedException("리프레쉬 토큰이 존재하지 않습니다.");
        }

        String refreshToken = req.getHeader("Authorization").substring("Bearer ".length());
        String userId = redisUtil.getData(refreshToken);

        if (userId == null) {
            throw new UnauthorizedException("리프레쉬 토큰이 유효하지 않습니다.");
        }

        userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new UnauthorizedException("리프레쉬 토큰이 유효하지 않습니다."));

        redisUtil.deleteData(refreshToken);
        return new ResponseDto("success", "ok", null);
    }

    @Override
    public Object refresh(HttpServletRequest req) {
        if (req.getHeader("Authorization") == null || !req.getHeader("Authorization").startsWith("Bearer")) {
            throw new UnauthorizedException("리프레쉬 토큰이 존재하지 않습니다.");
        }

        String refreshToken = req.getHeader("Authorization").substring("Bearer ".length());
        String userId = redisUtil.getData(refreshToken);

        if (userId == null) {
            throw new UnauthorizedException("리프레쉬 토큰이 유효하지 않습니다.");
        }

        userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new UnauthorizedException("리프레쉬 토큰이 유효하지 않습니다."));

        String accessToken = jwtUtil.generateAccessToken(Long.parseLong(userId));
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken(accessToken)
                .build();

        return new ResponseDto("success", "ok", tokenResponseDto);
    }

    @Override
    public Object sendVerificationMail(UserRequestDto userRequestDto) {
        userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("해당 이메일로 가입한 계정이 존재하지 않습니다."));
        String VERIFICATION_LINK = "http://localhost:8080/auth/verify/";
        UUID uuid = UUID.randomUUID();
        redisUtil.setDataExpire(uuid.toString(), userRequestDto.getEmail(), 60 * 30L);
        emailUtil.sendMail(userRequestDto.getEmail(), "[Pangtudy] 회원가입 인증메일입니다.", VERIFICATION_LINK + uuid);
        return new ResponseDto("success", "ok", null);
    }

    @Override
    public Object verifyEmail(String key) {
        String email = redisUtil.getData(key);
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("인증 메일이 만료되었습니다."));
        userEntity.setRole(UserRole.ROLE_USER);
        userRepository.save(userEntity);
        redisUtil.deleteData(key);
        return new ResponseDto("success", "ok", null);
    }

    @Override
    public Object sendPasswordChangeMail(UserRequestDto userRequestDto) {
        UserEntity userEntity = userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(() -> new NotFoundException("해당 이메일로 가입한 계정이 존재하지 않습니다."));
        String password = randomPassword(10);
        String salt = saltUtil.genSalt();
        userEntity.setSalt(salt);
        userEntity.setPassword(saltUtil.encodePassword(salt, password));
        userRepository.save(userEntity);
        emailUtil.sendMail(userRequestDto.getEmail(), "[Pangtudy] 임시비밀번호입니다.", "임시 비밀번호 : " + password);
        return new ResponseDto("success", "ok", null);
    }

    private String randomPassword(int length) {
        int index = 0;
        char[] charSet = new char[] {
                '0','1','2','3','4','5','6','7','8','9'
                ,'A','B','C','D','E','F','G','H','I','J','K','L','M'
                ,'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
                ,'a','b','c','d','e','f','g','h','i','j','k','l','m'
                ,'n','o','p','q','r','s','t','u','v','w','x','y','z'};

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            index = (int)(charSet.length * Math.random());
            sb.append(charSet[index]);
        }

        return sb.toString();
    }

    private <R, T> T sourceToDestinationTypeCasting(R source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }

}
