

DROP SCHEMA IF EXISTS mbtest;

CREATE SCHEMA mbtest;

CREATE TABLE mbtest.users (
  user_id serial PRIMARY KEY,
  name character varying(30)
);

INSERT INTO mbtest.users (name) values 
('Jimmy');


CREATE TABLE mbtest.sections (
  section_id int PRIMARY KEY,
  name character varying(30)
);

INSERT INTO mbtest.sections (section_id, name) values 
(1, 'Section 1');
