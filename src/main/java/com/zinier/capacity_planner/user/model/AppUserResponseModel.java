package com.zinier.capacity_planner.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserResponseModel {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String userRole;
    private Long employeeId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
