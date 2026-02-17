package com.zinier.capacity_planner.user.dao;

import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;

import java.util.List;
import java.util.Optional;

public interface AppUserDaoV1 {

    List<AppUserEntity> findAll();

    List<AppUserEntity> findAllActive();

    Optional<AppUserEntity> findById(Long id);

    Optional<AppUserEntity> findByUsername(String username);

    Optional<AppUserEntity> findActiveByUsername(String username);

    AppUserEntity save(AppUserEntity entity);

    boolean existsByUsername(String username);
}
