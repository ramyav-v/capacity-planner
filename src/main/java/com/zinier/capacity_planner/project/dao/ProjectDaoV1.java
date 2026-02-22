package com.zinier.capacity_planner.project.dao;

import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Region;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Status;
import java.util.List;
import java.util.Optional;

public interface ProjectDaoV1 {

    List<ProjectEntity> findAll();

    List<ProjectEntity> findAllActive();

    List<ProjectEntity> findByFilters(Region region, Status status, Boolean isActive);

    Optional<ProjectEntity> findById(Long id);

    ProjectEntity save(ProjectEntity project);

    boolean existsById(Long id);

    void deleteById(Long id);
}
