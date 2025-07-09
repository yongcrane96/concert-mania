package com.example.service.user;

import com.example.config.jwt.JwtProvider;
import com.example.domain.user.User;
import com.example.domain.user.UserRole;
import com.example.dto.UserDto;
import com.example.repository.UserRepository;
import com.example.service.TestFixtureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl 테스트")
class UserServiceImplTest {

    @InjectMocks private UserServiceImpl userService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = TestFixtureFactory.createTestUser();
    }

    @Nested
    class SignUp {

        @Test
        @DisplayName("회원가입 성공")
        void signupSuccess() {
            UserDto.SignUpRequest req = new UserDto.SignUpRequest("test@example.com", "pw1234");
            when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(req.getPassword())).thenReturn("encoded");

            userService.signup(req);

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("이메일 중복 시 예외")
        void signupFail() {
            UserDto.SignUpRequest req = new UserDto.SignUpRequest("dup@example.com", "pw1234");
            when(userRepository.existsByEmail(req.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.signup(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");
        }
    }

    @Nested
    class Login {

        @Test
        @DisplayName("로그인 성공")
        void loginSuccess() {
            UserDto.LoginRequest req = new UserDto.LoginRequest("test@example.com", "pw1234");
            when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtProvider.createToken(user.getEmail(), user.getRole())).thenReturn("token");

            UserDto.LoginResponse res = userService.login(req);

            assertThat(res).isNotNull();
            assertThat(res.getEmail()).isEqualTo(user.getEmail());
            assertThat(res.getRole()).isEqualTo(UserRole.USER.name());
        }

        @Test
        @DisplayName("비밀번호 불일치")
        void loginWrongPassword() {
            UserDto.LoginRequest req = new UserDto.LoginRequest("test@example.com", "wrong");
            when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(req.getPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.login(req))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
}
