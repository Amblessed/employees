-- Drop tables if they exist
DROP TABLE IF EXISTS authorities CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS system_users CASCADE;
DROP TABLE IF EXISTS employees CASCADE;

-- Create system_users
CREATE TABLE system_users (
      user_id VARCHAR(20) PRIMARY KEY,
      password VARCHAR(68) NOT NULL,
      active BOOLEAN NOT NULL,
      email VARCHAR(75) UNIQUE NOT NULL
);

-- Create roles
CREATE TABLE roles (
       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
       user_id VARCHAR(20) NOT NULL REFERENCES system_users (user_id),
       email VARCHAR(75) NOT NULL,
       user_role VARCHAR(50) NOT NULL,
       UNIQUE (user_id, user_role)
);


CREATE TABLE employees (
         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
         employee_id VARCHAR(20) NOT NULL UNIQUE,
         first_name VARCHAR(50) NOT NULL,
         last_name VARCHAR(50) NOT NULL,
         email VARCHAR(75) NOT NULL UNIQUE,
         phone_number VARCHAR(20) NOT NULL UNIQUE,
         password VARCHAR(68) NOT NULL,
         department VARCHAR(50) NOT NULL,
         position VARCHAR(50) NOT NULL,
         salary NUMERIC(15,2) NOT NULL,
         hire_date DATE NOT NULL,
         performance_review TEXT,
         skills TEXT,
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         active BOOLEAN DEFAULT TRUE,
         CONSTRAINT fk_employee_user FOREIGN KEY (employee_id) REFERENCES system_users(user_id)
 );
