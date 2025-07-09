package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class SignUpRequest {
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @Setter
    @AllArgsConstructor
    public static class LoginRequest {
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
    }

    @Getter
    public static class LoginResponse {
        private final String token;
        private final String email;
        private final String role;

        public LoginResponse(String token, String email, String role) {
            this.token = token;
            this.email = email;
            this.role = role;
        }
    }
}
