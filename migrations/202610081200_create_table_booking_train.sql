CREATE TABLE booking_train (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    booking_code VARCHAR(100),
    class_key VARCHAR(256),
    journey_code VARCHAR(256),
    carrier_number VARCHAR(50),
    train_name VARCHAR(100),
    sub_class VARCHAR(20),
    seat_class VARCHAR(20),
    provider INT,
    origin VARCHAR(100),
    destination VARCHAR(100),
    departure_datetime DATETIME,
    arrival_datetime DATETIME,
    total_amount DECIMAL(18,2) NULL,
    management_fee_amount DECIMAL(18,2) NULL,
    payment_method VARCHAR(20),
    booking_reference VARCHAR(256),
    reference_code VARCHAR(100),
    ota_reference VARCHAR(256),
    time_limit DATETIME,                       -- Biztrip reservation deadline, WIB wall time
    seats JSON,                                -- [{paxId, coachName, seatNumber}]
    status VARCHAR(20),
    approved_by BIGINT NULL,
    approved_by_email VARCHAR(256) NULL,
    approved_at TIMESTAMP NULL,
    rejected_by BIGINT NULL,
    rejected_by_email VARCHAR(256) NULL,
    rejected_at TIMESTAMP NULL,
    metadata JSON,
    error_message TEXT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_train_booking_id (booking_id),
    INDEX idx_booking_train_booking_code (booking_code),
    INDEX idx_booking_train_status_time_limit (status, time_limit)
);

CREATE TABLE booking_train_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    booking_code VARCHAR(100),
    status VARCHAR(20),
    data JSON,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_booking_train_history_booking_id (booking_id),
    INDEX idx_booking_train_history_booking_code (booking_code)
);

-- Train wallet (DEPOSIT) and credit (LIMIT) for every company that already has the flight one
INSERT IGNORE INTO company_balance (company_id, code, balance, currency, status, created_by, updated_by)
SELECT company_id, 'WALLET_TRAIN', 0, currency, status, 0, 0
FROM company_balance WHERE code = 'WALLET_FLIGHT';

INSERT IGNORE INTO company_credit (company_id, code, balance, currency, status, created_by, updated_by)
SELECT company_id, 'CREDIT_TRAIN', 0, currency, status, 0, 0
FROM company_credit WHERE code = 'CREDIT_FLIGHT';

-- Train management fee % for LIMIT starts equal to the flight one
INSERT IGNORE INTO company_config (company_id, code, value_str, value_int, value_bool, status, is_visible, created_by, updated_by)
SELECT company_id, 'CREDIT_TRAIN', value_str, value_int, value_bool, status, is_visible, 0, 0
FROM company_config WHERE code = 'CREDIT_FLIGHT';

-- Manual approval by default
INSERT IGNORE INTO company_config (company_id, code, value_bool, status, is_visible, created_by, updated_by)
SELECT company_id, 'IS_AUTO_APPROVE_BOOKING_TRAIN', 0, status, is_visible, 0, 0
FROM company_config WHERE code = 'IS_AUTO_APPROVE_BOOKING_FLIGHT';
