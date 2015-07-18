# --- First database schema

# --- !Ups

set ignorecase true;

CREATE TABLE employee (
  id                        BIGINT NOT NULL AUTO_INCREMENT,
  name                      VARCHAR(255) NOT NULL,
  address                   VARCHAR(1000) NOT NULL,
  designation               VARCHAR(255) NOT NULL,
  CONSTRAINT pk_employee PRIMARY KEY (id))
;

# --- !Downs

drop table if exists employee;
