/********************************************************************************
* Seed script - Initial SUPER_ADMIN user
* Run ONCE during fresh setup. Do NOT re-run on existing environments.
*
* Default credentials:
*   Username : admin
*   Password : Cap@Plan2026!
*
* IMPORTANT: Change the password after first login.
*
* To generate a new BCrypt hash for a different password, run:
*   java -cp build/libs/capacity-planner-*.jar \
*        com.zinier.capacity_planner.util.BcryptPasswordGenerator "YourNewPassword"
*
* Author: System
* Since: 2026-Feb-20
********************************************************************************/

USE capacity_planner;

-- BCrypt hash for: Cap@Plan2026!
-- Generated using BCryptPasswordEncoder (strength 10)
INSERT INTO app_user (username, password, full_name, user_role, is_active)
SELECT 'admin',
       '$2a$10$AQ0O/qkbBRnazeMcmb7EyeVpM/S0Wzs2JFYkWSGLp5dy3MCfxzzOy',
       'System Admin',
       'SUPER_ADMIN',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM app_user WHERE username = 'admin'
);
