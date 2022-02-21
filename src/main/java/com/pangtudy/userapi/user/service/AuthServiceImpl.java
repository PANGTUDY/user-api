package com.pangtudy.userapi.user.service;

import com.pangtudy.userapi.user.config.UserRole;
import com.pangtudy.userapi.user.model.TokenResult;
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
import javax.servlet.http.HttpServletRequest;
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
        Optional<UserEntity> userEntity = userRepository.findByEmail(param.getEmail());
        if (userEntity.isPresent()) {
            return null;
        }
        String password = param.getPassword();
        String salt = saltUtil.genSalt();
        param.setSalt(salt);
        param.setRole(UserRole.ROLE_USER);
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

                redisUtil.setDataExpire(refreshJwt, user.getEmail(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

                TokenResult tokenResult = new TokenResult(token, refreshJwt);
                return tokenResult;
            }
        }

        return null;
    }

    @Override
    public boolean logoutUser(HttpServletRequest req) {
        Cookie refreshToken = cookieUtil.getCookie(req, JwtUtil.REFRESH_TOKEN_NAME);

        if (refreshToken != null) {
            String refreshJwt = refreshToken.getValue();
            String refreshUname = redisUtil.getData(refreshJwt);

            if (refreshUname != null && refreshUname.equals(jwtUtil.getEmail(refreshJwt))) {
                redisUtil.deleteData(refreshJwt);
                return true;
            }
        }

        return false;
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

    @Override
    public void sendPasswordChangeMail(UserParam param) throws NotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(param.getEmail());
        if (Optional.ofNullable(user).isEmpty()) throw new NotFoundException("멤버가 조회되지않음");

        String password = randomPassword(10);
        modifyUserPassword(user.get(), password);
        emailService.sendMail(param.getEmail(), "[Pangtudy] 임시비밀번호입니다.", "임시 비밀번호 : " + password);
    }

    @Override
    public void modifyUserPassword(UserEntity user, String password) {
        String salt = saltUtil.genSalt();
        user.setSalt(salt);
        user.setPassword(saltUtil.encodePassword(salt, password));
        userRepository.save(user);
    }
	
	@Override
    public boolean tokenRefresh(HttpServletRequest req, HttpServletResponse res) {
		Cookie refreshToken = cookieUtil.getCookie(req, JwtUtil.REFRESH_TOKEN_NAME);
		
		if (refreshToken != null) {
			String refreshJwt = refreshToken.getValue();
            String refreshUname = redisUtil.getData(refreshJwt);
			
			if (refreshUname != null && refreshUname.equals(jwtUtil.getEmail(refreshJwt))) {
                UserEntity user = new UserEntity();
                user.setEmail(refreshUname);
                String newToken = jwtUtil.generateToken(user);

                Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, newToken);
                res.addCookie(newAccessToken);

                return true;
            }
		}
		
		return false;
    }

    public String randomPassword(int length) {
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
