package com.example.service.user;

import com.example.dto.UserDto;

public interface UserService {
    void signup(UserDto.SignUpRequest requestDto);
    UserDto.LoginResponse login(UserDto.LoginRequest requestDto);
}
