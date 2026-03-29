CREATE TABLE document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid UUID,
    company_id BIGINT NOT NULL,
    type VARCHAR(100),
    name VARCHAR(256) NOT NULL,
    status SMALLINT NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE booking_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    booking_code VARCHAR(100),
    type VARCHAR(100),
    file VARCHAR(256) NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);