CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    name VARCHAR(50) NOT NULL,
    age INT,
    gender VARCHAR(10),
    address VARCHAR(255),
    avatar VARCHAR(255),
    role INT DEFAULT 2,
    health_status INT DEFAULT 0,
    emergency_contact VARCHAR(50),
    emergency_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS health_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    heart_rate INT,
    blood_pressure_high DOUBLE,
    blood_pressure_low DOUBLE,
    blood_sugar DOUBLE,
    body_temperature DOUBLE,
    sleep_hours INT,
    sleep_quality INT,
    steps INT,
    calories DOUBLE,
    health_report TEXT,
    warning_level INT DEFAULT 0,
    measured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    device_id VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'online',
    device_value DOUBLE,
    location VARCHAR(100),
    last_online_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS emergency_call (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    call_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    latitude DOUBLE,
    longitude DOUBLE,
    description TEXT,
    handler VARCHAR(50),
    call_time TIMESTAMP NOT NULL,
    response_time TIMESTAMP,
    resolved_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS community_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    description TEXT,
    price DOUBLE,
    provider VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    service_time TIMESTAMP NOT NULL,
    address VARCHAR(255),
    remark TEXT,
    amount DOUBLE,
    payment_status VARCHAR(20) DEFAULT 'unpaid',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);