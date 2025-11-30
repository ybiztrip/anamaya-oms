CREATE TABLE company_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    code VARCHAR(50) NOT NULL,                -- e.g. WALLET_FLIGHT, WALLET_HOTEL
    balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'IDR',
    status SMALLINT DEFAULT 1,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_company_balance_company_id (company_id),
    UNIQUE KEY uq_company_balance_company_code (company_id, code)
);

CREATE TABLE company_balance_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance_id BIGINT NOT NULL,
    reference_id BIGINT,                      -- e.g. booking_id
    reference_code VARCHAR(100),              -- e.g. BOOK123, TXN20251109
    source_type VARCHAR(20) NOT NULL,            -- 1 = Booking, etc.
    type VARCHAR(20) NOT NULL,                -- CREDIT, DEBIT
    amount DECIMAL(18,2) NOT NULL,
    begin_balance DECIMAL(18,2) NOT NULL,
    end_balance DECIMAL(18,2) NOT NULL,
    remarks VARCHAR(255),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_balance_detail_balance_id (balance_id),
    INDEX idx_balance_detail_reference_id (reference_id),
    INDEX idx_balance_detail_source_type (source_type)
);
