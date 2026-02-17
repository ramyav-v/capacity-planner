package com.zinier.capacity_planner.user.repository;

import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepositoryV1 extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByUsername(String username);

    Optional<AppUserEntity> findByUsernameAndIsActiveTrue(String username);

    List<AppUserEntity> findByIsActiveTrue();

    boolean existsByUsername(String username);
}
