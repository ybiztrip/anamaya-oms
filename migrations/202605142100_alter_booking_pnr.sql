ALTER TABLE `booking_flight`
    ADD COLUMN `pnr_info` VARCHAR(100) AFTER `ota_reference`;

ALTER TABLE `booking_hotel`
    ADD COLUMN `itinerary_id` VARCHAR(100) AFTER `ota_reference`;