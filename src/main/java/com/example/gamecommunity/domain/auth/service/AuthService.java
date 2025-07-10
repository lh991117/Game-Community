package com.example.gamecommunity.domain.auth.service;

import com.example.gamecommunity.config.JwtUtil;
import com.example.gamecommunity.domain.auth.dto.request.LoginRequest;
import com.example.gamecommunity.domain.auth.dto.request.SignupRequest;
import com.example.gamecommunity.domain.auth.dto.response.LoginResponse;
import com.example.gamecommunity.domain.auth.dto.response.SignupResponse;
import com.example.gamecommunity.domain.user.entity.User;
import com.example.gamecommunity.domain.user.enums.UserRole;
import com.example.gamecommunity.domain.user.repository.UserRepository;
import com.example.gamecommunity.exception.AuthException;
import com.example.gamecommunity.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.ROLE_USER;

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                userRole,
                signupRequest.getNickname()
        );
        User savedUser = userRepository.save(newUser);

        return new SignupResponse(savedUser.getNickname(), savedUser.getUserRole().name());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저입니다.")
        );

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole(), user.getNickname());

        return new LoginResponse(bearerToken);
    }
}
