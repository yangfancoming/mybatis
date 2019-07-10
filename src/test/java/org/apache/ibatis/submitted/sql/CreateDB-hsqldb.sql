

DROP TABLE IF EXISTS users;

CREATE TABLE users (
  user_id identity PRIMARY KEY,
  name character varying(30)
);

INSERT INTO users (name) values ('Jimmy');
INSERT INTO users (name) values ('Iwao');
INSERT INTO users (name) values ('Kazuki');
