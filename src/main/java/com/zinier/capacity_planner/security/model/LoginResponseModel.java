package com.zinier.capacity_planner.security.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseModel {
    private String token;
    private String username;
    private String fullName;
    private String role;
}
