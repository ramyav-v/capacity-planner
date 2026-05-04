package com.zinier.capacity_planner.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectResponseModel {

    private Long id;
    private String name;
    private String code;
    private String region;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal psFee;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
