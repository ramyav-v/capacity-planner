package com.zinier.capacity_planner.project.repository;

import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Region;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepositoryV1 extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findByRegion(Region region);

    List<ProjectEntity> findByStatus(Status status);

    List<ProjectEntity> findByRegionAndStatus(Region region, Status status);

    List<ProjectEntity> findByIsActiveTrue();

    List<ProjectEntity> findByRegionAndIsActiveTrue(Region region);

    List<ProjectEntity> findByStatusAndIsActiveTrue(Status status);

    List<ProjectEntity> findByRegionAndStatusAndIsActiveTrue(Region region, Status status);
}
