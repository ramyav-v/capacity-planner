package com.zinier.capacity_planner.dashboard.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class RoleCountDtoV1 {
    private String role;
    private Long count;
}
