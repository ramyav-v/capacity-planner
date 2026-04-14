package com.zinier.capacity_planner.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiringGapByProjectModel {
    private Long projectId;
    private String projectName;
    private String projectCode;
    private List<HiringGapByRoleModel> roleGaps;
}
