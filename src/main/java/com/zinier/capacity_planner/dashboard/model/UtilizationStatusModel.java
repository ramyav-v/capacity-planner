package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UtilizationStatusModel {
    private Integer optimal;
    private Integer overUtilized;
    private Integer underUtilized;
}
