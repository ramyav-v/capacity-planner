package com.zinier.capacity_planner.financials.repository;

import com.zinier.capacity_planner.financials.dao.entity.ProjectResourcePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectResourcePlanRepositoryV1 extends JpaRepository<ProjectResourcePlanEntity, Long> {
    List<ProjectResourcePlanEntity> findByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);
}
