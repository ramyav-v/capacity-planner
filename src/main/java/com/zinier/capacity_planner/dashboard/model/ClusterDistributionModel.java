package com.zinier.capacity_planner.dashboard.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ClusterDistributionModel {
    private String region;
    private Integer count;
    private List<String> names;
}
