package com.example.controller.user;

import com.example.dto.UserDto;
import com.example.service.user.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "사용자 인증 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Operation(summary = "회원가입", description = "새로운 사용자 회원가입을 처리합니다.")
    @PostMapping("/singup")
    public ResponseEntity<String> singup(@Valid @RequestBody UserDto.SignUpRequest requestDto){
        userServiceImpl.signup(requestDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "로그인", description = "사용자 로그인 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> login(@Valid @RequestBody UserDto.LoginRequest requestDto){
        return ResponseEntity.ok(userServiceImpl.login(requestDto));
    }

}
