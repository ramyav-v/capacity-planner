package com.zinier.capacity_planner.employee.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EmployeeDtoV1 {

    private String name;
    private String email;
    private String role;
    private String company;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
}
