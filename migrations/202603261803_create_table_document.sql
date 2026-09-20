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