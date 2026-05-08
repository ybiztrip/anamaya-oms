ALTER TABLE `booking_flight`
    ADD COLUMN `invoice_id` BIGINT NULL AFTER `payment_url`,
    ADD KEY `idx_booking_flight_invoice_id` (`invoice_id`),
    ADD KEY `idx_booking_flight_payment_method` (`payment_method`);

ALTER TABLE `booking_hotel`
    ADD COLUMN `invoice_id` BIGINT NULL AFTER `payment_url`,
    ADD KEY `idx_booking_hotel_invoice_id` (`invoice_id`),
    ADD KEY `idx_booking_hotel_payment_method` (`payment_method`);
