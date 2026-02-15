package com.zinier.capacity_planner.dashboard.dao;

import com.zinier.capacity_planner.dashboard.dto.DashboardMetricsDtoV1;
import com.zinier.capacity_planner.dashboard.dto.HeadcountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RegionCountDtoV1;
import com.zinier.capacity_planner.dashboard.dto.RoleCountDtoV1;
import com.zinier.capacity_planner.dashboard.model.UtilizationStatusModel;

import java.util.List;

public interface DashboardDaoV1 {

    DashboardMetricsDtoV1 fetchMetrics();
    List<RoleCountDtoV1> fetchRoleDistribution();
    List<RegionCountDtoV1> fetchRegionDistribution();
    List<Object[]> fetchAllocatedByRoleForQuarter();
    List<HeadcountDtoV1> fetchHeadcountByRole();

}
