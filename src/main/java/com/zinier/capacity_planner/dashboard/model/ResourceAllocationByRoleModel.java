package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAllocationByRoleModel {

    private String role;
    private Integer allocated;
    private Integer availableSeats;
    private Double percentage;
}
