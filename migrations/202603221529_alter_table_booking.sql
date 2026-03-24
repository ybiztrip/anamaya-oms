ALTER TABLE `booking_flight`
ADD COLUMN `payment_method` VARCHAR(50) NULL AFTER `ota_reference`,
ADD COLUMN `payment_reference_1` VARCHAR(50) NULL AFTER `payment_method`,
ADD COLUMN `payment_reference_2` VARCHAR(50) NULL AFTER `payment_reference_1`,
ADD COLUMN `payment_url` VARCHAR(50) NULL AFTER `payment_reference_2`;

ALTER TABLE `booking_hotel`
ADD COLUMN `payment_method` VARCHAR(50) NULL AFTER `ota_reference`,
ADD COLUMN `payment_reference_1` VARCHAR(50) NULL AFTER `payment_method`,
ADD COLUMN `payment_reference_2` VARCHAR(50) NULL AFTER `payment_reference_1`,
ADD COLUMN `payment_url` VARCHAR(50) NULL AFTER `payment_reference_2`;
