-- Drop tables if they exist
DROP TABLE IF EXISTS authorities CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS system_users CASCADE;
DROP TABLE IF EXISTS employees CASCADE;

-- Create system_users
CREATE TABLE system_users (
      user_id VARCHAR(50) PRIMARY KEY,
      password VARCHAR(68) NOT NULL,
      active BOOLEAN NOT NULL
);

-- fun123

INSERT INTO system_users (user_id, password, active)
VALUES
    ('john', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('mary', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('susan', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('bright', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true);

-- Create roles
CREATE TABLE roles (
       user_id VARCHAR(50) NOT NULL,
       role VARCHAR(50) NOT NULL,
       UNIQUE (user_id, role),
       FOREIGN KEY (user_id) REFERENCES system_users (user_id)
);

INSERT INTO roles (user_id, role)
VALUES
    ('john', 'ROLE_EMPLOYEE'),
    ('mary', 'ROLE_EMPLOYEE'),
    ('mary', 'ROLE_MANAGER'),
    ('susan', 'ROLE_EMPLOYEE'),
    ('susan', 'ROLE_MANAGER'),
    ('susan', 'ROLE_ADMIN'),
    ('bright', 'ROLE_EMPLOYEE'),
    ('bright', 'ROLE_MANAGER'),
    ('bright', 'ROLE_ADMIN');


CREATE TABLE employees (
         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
         first_name VARCHAR(50) NOT NULL,
         last_name VARCHAR(50) NOT NULL,
         email VARCHAR(100) NOT NULL UNIQUE,
         phone_number VARCHAR(20) NOT NULL UNIQUE,
         department VARCHAR(50) NOT NULL,
         position VARCHAR(50) NOT NULL,
         salary NUMERIC(15,2) NOT NULL,
         hire_date DATE NOT NULL,
         performance_review TEXT,
         skills TEXT,
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         active BOOLEAN DEFAULT TRUE
 );
