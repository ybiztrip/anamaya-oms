ALTER TABLE `user`
ADD COLUMN `type` VARCHAR(50) NULL AFTER `country_code`,
ADD COLUMN `title` VARCHAR(10) NULL AFTER `type`,
ADD COLUMN `identity_no` VARCHAR(100) NULL AFTER `title`,
ADD COLUMN `passport_no` VARCHAR(100) NULL AFTER `phone_no`,
ADD COLUMN `passport_expiry` DATE NULL AFTER `passport_no`,
ADD COLUMN `date_of_birth` DATE NULL AFTER `passport_expiry`,
ADD COLUMN `nationality_code` VARCHAR(10) NULL AFTER `date_of_birth`;