CREATE TABLE booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    code VARCHAR(100),
    journey_code VARCHAR(100),
    contact_email VARCHAR(256) NOT NULL,
    contact_first_name VARCHAR(256) NOT NULL,
    contact_last_name VARCHAR(256) NOT NULL,
    contact_title VARCHAR(50) NOT NULL,
    contact_nationality VARCHAR(50) NOT NULL,
    contact_phone_code VARCHAR(4) NOT NULL,
    contact_phone_number VARCHAR(20) NOT NULL,
    contact_dob DATE NULL,
    additional_info JSON,
    client_additional_info JSON,
    status VARCHAR(20) NOT NULL,
    payment_expiration_time TIMESTAMP,
    approved_by BIGINT,
    approved_by_name VARCHAR(256),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE booking_pax (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    email VARCHAR(256),
    first_name VARCHAR(256) NOT NULL,
    last_name VARCHAR(256) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(50) NOT NULL,
    nationality VARCHAR(50),
    phone_code VARCHAR(4),
    phone_number VARCHAR(20),
    dob DATE,
    add_on JSON,
    issuing_country VARCHAR(50),
    document_type VARCHAR(50),
    document_no VARCHAR(128),
    expiration_date DATE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_id (booking_id)
);

CREATE TABLE booking_flight (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    type SMALLINT,
    client_source VARCHAR(50),
    item_id VARCHAR(256),
    room_id VARCHAR(256),
    origin VARCHAR(100),
    destination VARCHAR(100),
    departure_datetime TIMESTAMP,
    arrival_datetime TIMESTAMP,
    adult_amount DECIMAL(18,2) NULL
    child_amount DECIMAL(18,2) NULL
    infant_amount DECIMAL(18,2) NULL
    total_amount DECIMAL(18,2) NULL
    booking_reference VARCHAR(256) NOT NULL,
    ota_reference VARCHAR(256) NOT NULL,
    status VARCHAR(20),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_flight_booking_id (booking_id)
);

CREATE TABLE booking_flight_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    status VARCHAR(20),
    data text,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_flight_history_booking_id (booking_id)
)

CREATE TABLE booking_hotel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    client_source VARCHAR(50),
    item_id VARCHAR(256) NOT NULL,
    room_id VARCHAR(256) NOT NULL,
    rate_key TEXT,
    payment_key TEXT,
    num_room SMALLINT,
    check_in_date DATE,
    check_out_date DATE,
    partner_sell_amount DOUBLE,
    partner_nett_amount DOUBLE,
    currency VARCHAR(4),
    special_request TEXT,
    booking_reference VARCHAR(256),
    ota_reference VARCHAR(256),
    status SMALLINT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_hotel_booking_id (booking_id)
);
