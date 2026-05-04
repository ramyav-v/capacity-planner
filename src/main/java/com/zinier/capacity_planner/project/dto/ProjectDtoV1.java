package com.zinier.capacity_planner.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ProjectDtoV1 {

    private String name;
    private String code;
    private String region;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal psFee;
}
