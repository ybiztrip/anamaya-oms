CREATE TABLE access (
  id bigint NOT NULL AUTO_INCREMENT,
  label varchar(32) NOT NULL,
  access_key varchar(256) NOT NULL,
  parent_access_key varchar(256) DEFAULT '',
  position bigint DEFAULT NULL,
  created_by bigint DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by bigint DEFAULT NULL,
  updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

 INSERT INTO access( label, access_key, parent_access_key, position, created_by, created_at, updated_by, updated_at ) VALUES( "Create", "ORDER_CREATE", "", 1, 0, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP );
 INSERT INTO access( label, access_key, parent_access_key, position, created_by, created_at, updated_by, updated_at ) VALUES( "Approve", "ORDER_APPROVE", "", 2, 0, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP );
 INSERT INTO access( label, access_key, parent_access_key, position, created_by, created_at, updated_by, updated_at) VALUES( "User", "USER_CREATE", "", 1, 0, CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP );
