package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.UserEntity;
import com.pangtudy.userapi.user.model.UserParam;
import com.pangtudy.userapi.user.model.UserResult;
import com.pangtudy.userapi.user.repository.UserRepository;
import com.pangtudy.userapi.user.util.CookieUtil;
import com.pangtudy.userapi.user.util.JwtUtil;
import com.pangtudy.userapi.user.util.RedisUtil;
import com.pangtudy.userapi.user.util.SaltUtil;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SaltUtil saltUtil;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Override
    @Transactional
    public Object signUpUser(UserParam param) {
        String password = param.getPassword();
        String salt = saltUtil.genSalt();
        param.setSalt(salt);
        param.setPassword(saltUtil.encodePassword(salt, password));
        UserEntity user = userRepository.save(sourceToDestinationTypeCasting(param, new UserEntity()));
        return sourceToDestinationTypeCasting(user, new UserResult());
    }

    @Override
    public Object loginUser(UserParam param, HttpServletResponse res) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(param.getEmail());
        if (Optional.ofNullable(userEntity).isPresent()) {
            UserEntity user = userEntity.get();
            String salt = user.getSalt();
            String password = saltUtil.encodePassword(salt, param.getPassword());

            if (user.getPassword().equals(password)) {
                final String token = jwtUtil.generateToken(user);
                final String refreshJwt = jwtUtil.generateRefreshToken(user);
                Cookie accessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
                Cookie refreshToken = cookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);
                redisUtil.setDataExpire(refreshJwt, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);
                res.addCookie(accessToken);
                res.addCookie(refreshToken);

                return sourceToDestinationTypeCasting(user, new UserResult());
            }
        }

        return null;
    }

    @Override
    public void sendVerificationMail(UserParam param) throws NotFoundException {
        if (userRepository.findByEmail(param.getEmail()) == null) throw new NotFoundException("멤버가 조회되지 않음");

        String VERIFICATION_LINK = "http://localhost:8080/auth/verify/";
        UUID uuid = UUID.randomUUID();
        redisUtil.setDataExpire(uuid.toString(), param.getEmail(), 60 * 30L);
        emailService.sendMail(param.getEmail(), "[Pangtudy] 회원가입 인증메일입니다.", VERIFICATION_LINK + uuid.toString());
    }

    @Override
    public void verifyEmail(String key) throws NotFoundException {
        String email = redisUtil.getData(key);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (Optional.ofNullable(user).isEmpty()) throw new NotFoundException("멤버가 조회되지않음");
        modifyUserRole(user.get(), UserRole.ROLE_USER);
        redisUtil.deleteData(key);
    }

    @Override
    public void modifyUserRole(UserEntity user, UserRole userRole){
        user.setRole(userRole);
        userRepository.save(user);
    }

    private <R, T> T sourceToDestinationTypeCasting(R source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }
}
