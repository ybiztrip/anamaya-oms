CREATE TABLE company_balance_recap_daily (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id    BIGINT        NOT NULL,
    balance_id    BIGINT        NOT NULL,
    code          VARCHAR(50)   NOT NULL,
    recap_date    DATE          NOT NULL,
    begin_balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    end_balance   DECIMAL(18,2) NOT NULL DEFAULT 0,
    currency      VARCHAR(10)   DEFAULT 'IDR',
    created_by    BIGINT,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_by    BIGINT,
    updated_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    KEY idx_recap_company_id (company_id),
    KEY idx_recap_balance_id (balance_id),
    UNIQUE KEY uq_recap_balance_date (balance_id, recap_date)
);