CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  company_id BIGINT NULL,
  email VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL,
  first_name VARCHAR(256) NULL,
  last_name VARCHAR(256) NULL,
  gender VARCHAR(20) NULL,
  position_id BIGINT NULL,
  phone_no VARCHAR(256) NULL,
  status SMALLINT NOT NULL,
  created_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email),
  KEY idx_users_company_id (company_id)
);