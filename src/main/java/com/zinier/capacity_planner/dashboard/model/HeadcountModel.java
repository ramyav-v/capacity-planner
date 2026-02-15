package com.zinier.capacity_planner.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class HeadcountModel {

    private String role;
    private Integer zinier;
    private Integer nonZinier;
    private Integer total;
}
