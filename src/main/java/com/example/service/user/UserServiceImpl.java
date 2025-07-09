package com.example.service.user;

import com.example.config.jwt.JwtProvider;
import com.example.domain.user.User;
import com.example.domain.user.UserRole;
import com.example.dto.UserDto;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void signup(UserDto.SignUpRequest requestDto){
        // 이메일 중복 확인
        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        UserRole role = UserRole.USER;
        if ("admin@example.com".equals(requestDto.getEmail())) {
            role = UserRole.ADMIN;
        }

        // 사용자  저장
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .role(role)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDto.LoginResponse login(UserDto.LoginRequest requestDto){
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.createToken(user.getEmail(), user.getRole());

        return new UserDto.LoginResponse(token, user.getEmail(), user.getRole().name());
    }

}
