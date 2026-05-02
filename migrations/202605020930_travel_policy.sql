CREATE TABLE travel_policy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    name VARCHAR(100),
    flights JSON,
    flight_minimum_price INT UNSIGNED,
    flight_maximum_price INT UNSIGNED,
    flight_minimum_class VARCHAR(100),
    flight_maximum_class VARCHAR(100),
    hotel_minimum_price INT UNSIGNED,
    hotel_maximum_price INT UNSIGNED,
    hotel_minimum_class VARCHAR(100),
    hotel_maximum_class VARCHAR(100),
    hotel_pagu TEXT,
    status SMALLINT DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_travel_policy_company_id (company_id),
);

CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    type VARCHAR(100),
    reference_id BIGINT NOT NULL,
    data json,
    status SMALLINT DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_activity_log_reference_id_company_id (reference_id, company_id),
);