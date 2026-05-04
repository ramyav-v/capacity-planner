-- Add PS Fee column to project table
ALTER TABLE project ADD COLUMN ps_fee DECIMAL(15,2) NULL;

-- Create role_cost_config table for project financials
CREATE TABLE IF NOT EXISTS role_cost_config (
    role           VARCHAR(50)    NOT NULL PRIMARY KEY,
    cost_rate      DECIMAL(10,2)  NOT NULL,
    hours_per_week INT            NOT NULL,
    updated_at     TIMESTAMP      NULL
);

-- Add margin_rate column to role_cost_config (skip if already exists)
-- ALTER TABLE role_cost_config ADD COLUMN margin_rate DECIMAL(10,2) NULL;

-- Add total_hours column to role_cost_config (standard hours for Non-Discounted plan)
ALTER TABLE role_cost_config ADD COLUMN total_hours DECIMAL(8,2) NULL;

-- Create project_resource_plan table (planned resource allocation per project, independent of capacity inputs)
CREATE TABLE IF NOT EXISTS project_resource_plan (
    id             BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    project_id     BIGINT         NOT NULL,
    role           VARCHAR(50)    NOT NULL,
    total_hours    DECIMAL(8,2)   NOT NULL,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
