package com.zinier.capacity_planner.dashboard.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class RegionCountDtoV1 {
    private String region;
    private Long count;
}
