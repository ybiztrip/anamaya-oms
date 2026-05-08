ALTER TABLE `booking_flight`
    ADD COLUMN `error_message` TEXT NULL;

ALTER TABLE `booking_hotel`
    ADD COLUMN `error_message` TEXT NULL;
