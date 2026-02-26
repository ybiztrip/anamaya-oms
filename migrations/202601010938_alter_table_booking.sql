ALTER TABLE `booking`
ADD COLUMN `rejected_by` BIGINT NULL AFTER `approved_by_name`,
ADD COLUMN `rejected_by_name` VARCHAR(256) NULL AFTER `rejected_by`;
