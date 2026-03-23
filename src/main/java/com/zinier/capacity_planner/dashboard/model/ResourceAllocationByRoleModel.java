package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAllocationByRoleModel {

    private String role;
    private Integer allocated;
    private Integer availableSeats;
    private Double percentage;
    private List<String> availableNames;
}
