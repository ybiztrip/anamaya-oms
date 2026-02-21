ALTER TABLE `anamaya_oms`.`user`
ADD COLUMN `title` VARCHAR(10) NULL AFTER `country_code`,
ADD COLUMN `identity_no` VARCHAR(100) NULL AFTER `title`,
ADD COLUMN `passport_no` VARCHAR(100) NULL AFTER `phone_no`,
ADD COLUMN `passport_expiry` DATE NULL AFTER `passport_no`,
ADD COLUMN `date_of_birth` DATE NULL AFTER `passport_expiry`;