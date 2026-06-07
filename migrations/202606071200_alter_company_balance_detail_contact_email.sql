ALTER TABLE company_balance_detail
    ADD COLUMN contact_email VARCHAR(256) NULL AFTER reference_code;

CREATE INDEX idx_cbd_contact_email ON company_balance_detail (contact_email);

-- Backfill existing booking-derived rows. reference_id = booking.id for
-- BOOKING (debit) and BOOKING_FAILED (rollback) rows.
UPDATE company_balance_detail d
JOIN booking b ON b.id = d.reference_id
SET d.contact_email = b.contact_email
WHERE d.source_type IN ('BOOKING', 'BOOKING_FAILED');

-- Backfill refund rows. reference_id = refund.id, so go via refund.booking_code.
UPDATE company_balance_detail d
JOIN refund r ON r.id = d.reference_id
JOIN booking b ON b.code = r.booking_code
SET d.contact_email = b.contact_email
WHERE d.source_type = 'BOOKING_REFUND';
