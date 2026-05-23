ALTER TABLE `booking_flight`
    ADD COLUMN `refund_id` BIGINT NULL AFTER `invoice_id`,
    ADD KEY `idx_booking_flight_refund_id` (`refund_id`);

ALTER TABLE `booking_hotel`
    ADD COLUMN `refund_id` BIGINT NULL AFTER `invoice_id`,
    ADD KEY `idx_booking_hotel_refund_id` (`refund_id`);
