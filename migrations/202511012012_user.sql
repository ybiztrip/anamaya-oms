CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  company_id BIGINT NULL,
  email VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL,
  first_name VARCHAR(256) NULL,
  last_name VARCHAR(256) NULL,
  gender VARCHAR(20) NULL,
  position_id BIGINT NULL,
  country_code VARCHAR(10) NULL,
  phone_no VARCHAR(256) NULL,
  status SMALLINT NOT NULL,
  enable_chat_engine TINYINT,
  created_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email),
  KEY idx_users_company_id (company_id)
);

ALTER TABLE `anamaya_oms`.`user`
ADD UNIQUE INDEX `uq_users_phone_no` (`phone_no` ASC) VISIBLE;

INSERT INTO `user` (`id`, company_id`, `email`, `password`, `first_name`, `last_name`, `gender`, `position_id`, `phone_no`, `status`, `created_by`, `created_at`, `updated_by`, `updated_at`) VALUES ('1', '1', 'sysadmin@anamaya.ai', '$2a$10$BBbkiXR7WT9qN6of3OMkcOfFxroEZzmNyHgEDGncUjxjHHmGf8ps6', 'sysadmin', 'anamaya', 'MALE', '0', '+628123456789', '1', '0', '2025-11-01 13:29:34', '0', '2025-11-02 15:26:00');
