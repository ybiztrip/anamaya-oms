ALTER TABLE company_balance_detail
    ADD COLUMN booking_type VARCHAR(20) NULL AFTER source_type;

CREATE INDEX idx_cbd_booking_type ON company_balance_detail (booking_type);
CREATE INDEX idx_cbd_source_created ON company_balance_detail (source_type, created_at);

ALTER TABLE company_credit_detail
    ADD COLUMN booking_type VARCHAR(20) NULL AFTER source_type;

CREATE INDEX idx_ccd_booking_type ON company_credit_detail (booking_type);
CREATE INDEX idx_ccd_source_created ON company_credit_detail (source_type, created_at);