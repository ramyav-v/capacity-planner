package com.zinier.capacity_planner.financials.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_cost_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCostConfigEntity {

    @Id
    @Column(length = 50)
    private String role;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costRate;

    /** Margin rate = what is billed to client (used for Non-Discounted / Planned view) */
    @Column(precision = 10, scale = 2)
    private BigDecimal marginRate;

    @Column(nullable = false)
    private Integer hoursPerWeek;

    @Column(precision = 8, scale = 2)
    private BigDecimal totalHours;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
