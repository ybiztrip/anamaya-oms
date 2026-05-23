CREATE TABLE refund (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id         BIGINT NOT NULL,
    code               VARCHAR(50) NOT NULL,
    booking_type       VARCHAR(20) NOT NULL,
    booking_code       VARCHAR(100) NOT NULL,
    payment_method     VARCHAR(20) NOT NULL,
    requested_amount   DECIMAL(18,2) NOT NULL,
    paid_amount        DECIMAL(18,2) NULL,
    currency           VARCHAR(10) DEFAULT 'IDR',
    status             VARCHAR(20) NOT NULL,
    remarks            VARCHAR(255),
    paid_at            TIMESTAMP NULL,
    cancelled_at       TIMESTAMP NULL,
    created_by         BIGINT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by         BIGINT,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uq_refund_company_code (company_id, code),
    KEY idx_refund_status (status),
    KEY idx_refund_booking (booking_code, company_id)
);
