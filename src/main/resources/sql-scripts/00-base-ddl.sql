/********************************************************************************
* Base scripts to setup user, database and schema.
* Author: Ramya V (ramya.v@zinier.com)
* Since: 2023-Sep-05
********************************************************************************/


CREATE DATABASE IF NOT EXISTS capacity_planner;

CREATE USER IF NOT EXISTS 'capacity_user'@'localhost' IDENTIFIED BY 'cap123';

GRANT ALL PRIVILEGES ON capacity_planner.* TO 'capacity_user'@'localhost';

FLUSH PRIVILEGES;
