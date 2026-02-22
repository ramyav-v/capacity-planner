/********************************************************************************
* Table info - todo
* Table: (table name)
* Author: Ramya V (ramya.v@zenier.com)
* Since: 2023-Dec-07
********************************************************************************/

-- =========================================
-- EMPLOYEE
-- =========================================
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(200),
    role ENUM('PM','TL','Dev','QA','Support') NOT NULL,
    company ENUM('Zinier','Sankey','TopGrep') NOT NULL DEFAULT 'Zinier',
    region ENUM('US','EMEA','AU') NOT NULL DEFAULT 'US',
    start_date DATE NOT NULL,
    end_date DATE NULL,
    capacity_factor DECIMAL(3,2) NOT NULL DEFAULT 1.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_employee_role (role),
    INDEX idx_employee_company (company),
    INDEX idx_employee_name (name)
);

-- =========================================
-- PROJECT
-- =========================================
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    region ENUM('US','EMEA','AU') NOT NULL DEFAULT 'US',
    status ENUM('ACTIVE','ON_HOLD','COMPLETED','CANCELLED') DEFAULT 'ACTIVE',
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================
-- WEEKLY ALLOCATION
-- =========================================
CREATE TABLE IF NOT EXISTS weekly_allocation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    week_start_date DATE NOT NULL,
    allocation_pct DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee FOREIGN KEY (employee_id)
        REFERENCES employee(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_project FOREIGN KEY (project_id)
        REFERENCES project(id)
        ON DELETE CASCADE,

    UNIQUE KEY uk_emp_proj_week (employee_id, project_id, week_start_date),

    INDEX idx_alloc_employee (employee_id),
    INDEX idx_alloc_project (project_id),
    INDEX idx_alloc_week (week_start_date)
);

-- =========================================
-- APP USER
-- =========================================
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    user_role ENUM('SUPER_ADMIN','ADMIN','MANAGER','VIEWER')
            NOT NULL DEFAULT 'VIEWER',
    employee_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_employee FOREIGN KEY (employee_id)
        REFERENCES employee(id)
);
