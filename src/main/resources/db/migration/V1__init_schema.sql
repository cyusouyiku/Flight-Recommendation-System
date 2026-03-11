-- Flyway 迁移 V1：初始化表结构
CREATE TABLE IF NOT EXISTS airport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(3) NOT NULL UNIQUE,
    name VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    timezone VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS airline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(2) NOT NULL UNIQUE,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS route (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_airport_code VARCHAR(3),
    arrival_airport_code VARCHAR(3),
    departure_airport_name VARCHAR(255),
    arrival_airport_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS flight (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    departure_time TIMESTAMP,
    arrival_time TIMESTAMP,
    duration_minutes INT,
    effective_date DATE,
    flight_number VARCHAR(32),
    airline_code VARCHAR(8),
    cabin_class VARCHAR(32),
    aircraft_type VARCHAR(32),
    stops INT,
    is_direct BOOLEAN,
    departure_airport VARCHAR(255),
    arrival_airport VARCHAR(255),
    departure_airport_code VARCHAR(3),
    arrival_airport_code VARCHAR(3),
    price INT,
    original_price INT,
    available_seats INT,
    status VARCHAR(32),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64),
    password VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(32),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    flight_number VARCHAR(32),
    cabin_class VARCHAR(32),
    passenger_name VARCHAR(255),
    status VARCHAR(32),
    amount INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS flight_price (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(32),
    departure_airport_code VARCHAR(3),
    arrival_airport_code VARCHAR(3),
    cabin_class VARCHAR(32),
    price INT,
    record_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_flight_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    departure_airport_code VARCHAR(3),
    arrival_airport_code VARCHAR(3),
    behavior_type VARCHAR(32),
    flight_number VARCHAR(32),
    created_at TIMESTAMP
);
