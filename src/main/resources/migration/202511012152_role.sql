CREATE TABLE role (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(35) NOT NULL,
  is_super_admin char(1) NOT NULL DEFAULT '0',
  created_by bigint DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE role_access (
  id bigint NOT NULL AUTO_INCREMENT,
  role_id bigint NOT NULL,
  access_key varchar(256) NOT NULL,
  created_by bigint DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_role_id (role_id)
);

 INSERT INTO role( name, is_super_admin) VALUES ("Super Admin", 1);
 INSERT INTO role ( name, is_super_admin) VALUES ("Company Admin", 0);
 INSERT INTO role ( name, is_super_admin) VALUES ("Approver", 0);
 INSERT INTO role ( name, is_super_admin) VALUES ("User", 0);

