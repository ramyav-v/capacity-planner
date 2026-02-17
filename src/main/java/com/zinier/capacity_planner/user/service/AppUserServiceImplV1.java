package com.zinier.capacity_planner.user.service;

import com.zinier.capacity_planner.user.dao.AppUserDaoV1;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity.UserRole;
import com.zinier.capacity_planner.user.dto.AppUserDtoV1;
import com.zinier.capacity_planner.user.model.AppUserResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserServiceImplV1 implements AppUserServiceV1 {

    private final AppUserDaoV1 appUserDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<AppUserResponseModel> listAll() {
        return appUserDao.findAllActive().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserResponseModel getById(Long id) {
        AppUserEntity entity = appUserDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toModel(entity);
    }

    @Override
    @Transactional
    public AppUserResponseModel create(AppUserDtoV1 request) {
        if (appUserDao.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserRole role = parseUserRole(request.getUserRole());

        AppUserEntity entity = AppUserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .userRole(role)
                .employeeId(request.getEmployeeId())
                .isActive(true)
                .build();

        return toModel(appUserDao.save(entity));
    }

    @Override
    @Transactional
    public AppUserResponseModel update(Long id, AppUserDtoV1 request) {
        AppUserEntity existing = appUserDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        existing.setFullName(request.getFullName());
        existing.setUserRole(parseUserRole(request.getUserRole()));
        existing.setEmployeeId(request.getEmployeeId());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toModel(appUserDao.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AppUserEntity existing = appUserDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        existing.setIsActive(false);
        appUserDao.save(existing);
    }

    private AppUserResponseModel toModel(AppUserEntity entity) {
        return AppUserResponseModel.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .userRole(entity.getUserRole().name())
                .employeeId(entity.getEmployeeId())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserRole parseUserRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid userRole: " + role + ". Must be one of: SUPER_ADMIN, ADMIN, VIEWER");
        }
    }
}
