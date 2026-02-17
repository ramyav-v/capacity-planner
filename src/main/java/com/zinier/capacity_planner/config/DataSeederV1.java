package com.zinier.capacity_planner.config;

import com.zinier.capacity_planner.user.dao.AppUserDaoV1;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity;
import com.zinier.capacity_planner.user.dao.entity.AppUserEntity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeederV1 implements CommandLineRunner {

    private final AppUserDaoV1 appUserDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (appUserDao.findAllActive().isEmpty()) {
            AppUserEntity admin = AppUserEntity.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .userRole(UserRole.SUPER_ADMIN)
                    .isActive(true)
                    .build();

            appUserDao.save(admin);
            log.info("Seed SUPER_ADMIN user created: username=admin, password=admin123");
        }
    }
}
