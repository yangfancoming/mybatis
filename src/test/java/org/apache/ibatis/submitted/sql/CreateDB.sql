

DROP SCHEMA IF EXISTS mbtest;

CREATE SCHEMA mbtest;

CREATE TABLE mbtest.users (
  user_id serial PRIMARY KEY,
  name character varying(30)
);

INSERT INTO mbtest.users (name) values ('Jimmy');
INSERT INTO mbtest.users (name) values ('Iwao');
INSERT INTO mbtest.users (name) values ('Kazuki');
