package com.zinier.capacity_planner.employee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponseModel {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String company;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
