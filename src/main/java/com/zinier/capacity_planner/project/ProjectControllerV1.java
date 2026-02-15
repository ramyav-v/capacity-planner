package com.zinier.capacity_planner.project;

import com.zinier.capacity_planner.project.dto.ProjectDtoV1;
import com.zinier.capacity_planner.project.model.ProjectResponseModel;
import com.zinier.capacity_planner.project.service.ProjectServiceV1;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project APIs.
 */
@RestController
@RequestMapping("${spring.data.rest.base-path}/v1/projects")
@RequiredArgsConstructor
public class ProjectControllerV1 {

    private final ProjectServiceV1 service;

    /**
     * List all projects with optional filters
     * GET /api/v1/projects?region=US&status=ACTIVE
     */
    @GetMapping
    public List<ProjectResponseModel> listAll(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive) {
        return service.listAll(region, status, isActive).stream()
                .collect(Collectors.toList());
    }

    /**
     * Get project by ID
     * GET /api/v1/projects/{id}
     */
    @GetMapping("/{id}")
    public ProjectResponseModel getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Create new project
     * POST /api/v1/projects
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseModel create(@RequestBody ProjectDtoV1 request) {
        return service.create(toModel(request, null));
    }

    /**
     * Update project
     * PUT /api/v1/projects/{id}
     */
    @PutMapping("/{id}")
    public ProjectResponseModel update(@PathVariable Long id, @RequestBody ProjectDtoV1 request) {
        return service.update(id, toModel(request, id));
    }

    /**
     * Soft-delete project (sets is_active = false)
     * DELETE /api/v1/projects/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    private ProjectResponseModel toModel(ProjectDtoV1 request, Long id) {
        return ProjectResponseModel.builder()
                .id(id)
                .name(request.getName())
                .code(request.getCode())
                .region(request.getRegion())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }
}
