ALTER TABLE `booking_flight`
    MODIFY COLUMN `management_fee_amount` DECIMAL(18,2) NULL;

ALTER TABLE `booking_hotel`
    MODIFY COLUMN `management_fee_amount` DECIMAL(18,2) NULL;
