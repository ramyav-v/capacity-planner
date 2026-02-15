package com.zinier.capacity_planner.capacityInputs.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CapacityInputDtoV1 {
    private Integer employeeId;
    private Integer projectId;
    private LocalDate weekStartDate;
    private BigDecimal allocationPct;
}
