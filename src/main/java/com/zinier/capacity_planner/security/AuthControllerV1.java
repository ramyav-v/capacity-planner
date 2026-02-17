package com.zinier.capacity_planner.security;

import com.zinier.capacity_planner.security.dto.LoginRequestDtoV1;
import com.zinier.capacity_planner.security.model.LoginResponseModel;
import com.zinier.capacity_planner.user.dao.AppUserDaoV1;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtUtilV1 jwtUtil;
    private final AppUserDaoV1 appUserDao;

    @PostMapping("/login")
    public LoginResponseModel login(@RequestBody LoginRequestDtoV1 request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        AppUserEntity user = appUserDao.findActiveByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String role = user.getUserRole().name();
        String token = jwtUtil.generateToken(user.getUsername(), role);

        return LoginResponseModel.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(role)
                .build();
    }
}
