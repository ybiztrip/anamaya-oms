ALTER TABLE booking
    ADD COLUMN approved_at DATETIME NULL AFTER approved_by_email,
    ADD COLUMN rejected_at DATETIME NULL AFTER rejected_by_email;

ALTER TABLE booking_flight
    ADD COLUMN approved_by BIGINT NULL AFTER status,
    ADD COLUMN approved_by_email VARCHAR(256) NULL AFTER approved_by,
    ADD COLUMN approved_at DATETIME NULL AFTER approved_by_email,
    ADD COLUMN rejected_by BIGINT NULL AFTER approved_at,
    ADD COLUMN rejected_by_email VARCHAR(256) NULL AFTER rejected_by,
    ADD COLUMN rejected_at DATETIME NULL AFTER rejected_by_email;

ALTER TABLE booking_hotel
    ADD COLUMN approved_by BIGINT NULL AFTER status,
    ADD COLUMN approved_by_email VARCHAR(256) NULL AFTER approved_by,
    ADD COLUMN approved_at DATETIME NULL AFTER approved_by_email,
    ADD COLUMN rejected_by BIGINT NULL AFTER approved_at,
    ADD COLUMN rejected_by_email VARCHAR(256) NULL AFTER rejected_by,
    ADD COLUMN rejected_at DATETIME NULL AFTER rejected_by_email;
