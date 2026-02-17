package com.zinier.capacity_planner.user.dao;

import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import com.zinier.capacity_planner.user.repository.AppUserRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AppUserDaoImplV1 implements AppUserDaoV1 {

    private final AppUserRepositoryV1 repository;

    @Override
    public List<AppUserEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<AppUserEntity> findAllActive() {
        return repository.findByIsActiveTrue();
    }

    @Override
    public Optional<AppUserEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<AppUserEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Optional<AppUserEntity> findActiveByUsername(String username) {
        return repository.findByUsernameAndIsActiveTrue(username);
    }

    @Override
    public AppUserEntity save(AppUserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }
}
