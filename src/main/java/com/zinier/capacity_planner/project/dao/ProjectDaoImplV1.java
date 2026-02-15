package com.zinier.capacity_planner.project.dao;

import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Region;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Status;
import com.zinier.capacity_planner.project.repository.ProjectRepositoryV1;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectDaoImplV1 implements ProjectDaoV1 {

    private final ProjectRepositoryV1 repository;

    @Override
    public List<ProjectEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProjectEntity> findAllActive() {
        return repository.findByIsActiveTrue();
    }

    @Override
    public List<ProjectEntity> findByFilters(Region region, Status status, Boolean isActive) {
        if (region != null && status != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByRegionAndStatusAndIsActiveTrue(region, status);
        } else if (region != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByRegionAndIsActiveTrue(region);
        } else if (status != null && Boolean.TRUE.equals(isActive)) {
            return repository.findByStatusAndIsActiveTrue(status);
        } else if (region != null && status != null) {
            return repository.findByRegionAndStatus(region, status);
        } else if (region != null) {
            return repository.findByRegion(region);
        } else if (status != null) {
            return repository.findByStatus(status);
        } else if (Boolean.TRUE.equals(isActive)) {
            return repository.findByIsActiveTrue();
        }
        return repository.findAll();
    }

    @Override
    public Optional<ProjectEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ProjectEntity save(ProjectEntity project) {
        return repository.save(project);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
