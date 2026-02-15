package com.zinier.capacity_planner.project.service;

import com.zinier.capacity_planner.project.model.ProjectResponseModel;
import java.util.List;

public interface ProjectServiceV1 {

    List<ProjectResponseModel> listAll(String region, String status, Boolean isActive);

    ProjectResponseModel getById(Long id);

    ProjectResponseModel create(ProjectResponseModel project);

    ProjectResponseModel update(Long id, ProjectResponseModel project);

    void delete(Long id);
}
