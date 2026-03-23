package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UtilizationStatusModel {
    private Integer optimal;
    private Integer overUtilized;
    private Integer underUtilized;
    private List<String> optimalNames;
    private List<String> overUtilizedNames;
    private List<String> underUtilizedNames;
}
