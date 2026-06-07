ALTER TABLE booking
    CHANGE COLUMN approved_by_name approved_by_email VARCHAR(256) NULL,
    CHANGE COLUMN rejected_by_name rejected_by_email VARCHAR(256) NULL;

ALTER TABLE company_balance_detail
    ADD COLUMN triggered_by_email VARCHAR(256) NULL AFTER contact_email;

ALTER TABLE company_credit_detail
    ADD COLUMN contact_email VARCHAR(256) NULL AFTER reference_code,
    ADD COLUMN triggered_by_email VARCHAR(256) NULL AFTER contact_email;

-- Backfill existing booking-derived credit rows. reference_id = booking.id for
-- BOOKING (debit) and BOOKING_FAILED (rollback) rows.
UPDATE company_credit_detail d
JOIN booking b ON b.id = d.reference_id
SET d.contact_email = b.contact_email
WHERE d.source_type IN ('BOOKING', 'BOOKING_FAILED');

-- Backfill refund rows. reference_id = refund.id, so go via refund.booking_code.
UPDATE company_credit_detail d
JOIN refund r ON r.id = d.reference_id
JOIN booking b ON b.code = r.booking_code
SET d.contact_email = b.contact_email
WHERE d.source_type = 'BOOKING_REFUND';

ALTER TABLE booking_flight
    ADD COLUMN approved_by_email VARCHAR(256) NULL AFTER status;

ALTER TABLE booking_hotel
    ADD COLUMN approved_by_email VARCHAR(256) NULL AFTER status;
