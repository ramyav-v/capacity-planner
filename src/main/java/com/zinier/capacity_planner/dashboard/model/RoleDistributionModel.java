package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RoleDistributionModel {
    private String role;
    private Integer count;
}
