package com.zinier.capacity_planner.security.dto;

import lombok.Data;

@Data
public class LoginRequestDtoV1 {
    private String username;
    private String password;
}
