package com.zinier.capacity_planner.project.service;

import com.zinier.capacity_planner.project.dao.ProjectDaoV1;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Region;
import com.zinier.capacity_planner.project.dao.entity.ProjectEntity.Status;
import com.zinier.capacity_planner.project.model.ProjectResponseModel;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImplV1 implements ProjectServiceV1 {

    private final ProjectDaoV1 projectDao;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseModel> listAll(String region, String status, Boolean isActive) {
        Region regionEnum = region != null ? Region.valueOf(region) : null;
        Status statusEnum = status != null ? Status.valueOf(status) : null;

        return projectDao.findByFilters(regionEnum, statusEnum, isActive).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseModel getById(Long id) {
        ProjectEntity entity = projectDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        return toModel(entity);
    }

    @Override
    @Transactional
    public ProjectResponseModel create(ProjectResponseModel model) {
        ProjectEntity entity = toEntity(model);
        entity.setId(null);
        entity.setIsActive(true);
        return toModel(projectDao.save(entity));
    }

    @Override
    @Transactional
    public ProjectResponseModel update(Long id, ProjectResponseModel model) {
        ProjectEntity existing = projectDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        existing.setName(model.getName());
        existing.setCode(model.getCode());
        existing.setRegion(Region.valueOf(model.getRegion()));
        existing.setStatus(Status.valueOf(model.getStatus()));
        existing.setStartDate(model.getStartDate());
        existing.setEndDate(model.getEndDate());

        return toModel(projectDao.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProjectEntity existing = projectDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        existing.setIsActive(false);
        projectDao.save(existing);
    }

    private ProjectResponseModel toModel(ProjectEntity entity) {
        return ProjectResponseModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .region(entity.getRegion().name())
                .status(entity.getStatus().name())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ProjectEntity toEntity(ProjectResponseModel model) {
        return ProjectEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .code(model.getCode())
                .region(model.getRegion() != null ? Region.valueOf(model.getRegion()) : Region.US)
                .status(model.getStatus() != null ? Status.valueOf(model.getStatus()) : Status.ACTIVE)
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .isActive(model.getIsActive() != null ? model.getIsActive() : true)
                .build();
    }
}
