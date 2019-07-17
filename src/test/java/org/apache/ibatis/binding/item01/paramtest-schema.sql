
drop table if exists param_test;
  CREATE TABLE param_test (
  id   VARCHAR(255) NOT NULL,
  size BIGINT,
  PRIMARY KEY (id)
);
INSERT INTO param_test (id, size) VALUES ('foo', 9223372036854775807);
