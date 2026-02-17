package com.zinier.capacity_planner.capacityInputs.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder

public class CapacityInputResponseModel {
    private Long id;
    private LocalDate weekStartDate;
    private BigDecimal allocationPct;
}

