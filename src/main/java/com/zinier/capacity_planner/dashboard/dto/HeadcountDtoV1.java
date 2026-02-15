package com.zinier.capacity_planner.dashboard.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class HeadcountDtoV1 {

    private String role;
    private Long zinier;
    private Long nonZinier;
    private Long total;
}
