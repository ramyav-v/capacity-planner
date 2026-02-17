package com.zinier.capacity_planner.user.dto;

import lombok.Data;

@Data
public class AppUserDtoV1 {
    private String username;
    private String password;
    private String fullName;
    private String userRole;
    private Long employeeId;
}
