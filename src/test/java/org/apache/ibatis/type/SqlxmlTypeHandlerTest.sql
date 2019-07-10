

DROP SCHEMA IF EXISTS mbtest;

CREATE SCHEMA mbtest;

CREATE TABLE mbtest.test_sqlxml (
  id serial PRIMARY KEY,
  content XML
);

INSERT INTO mbtest.test_sqlxml (id, content)
VALUES (1, '<title>XML data</title>');

INSERT INTO mbtest.test_sqlxml (id, content)
VALUES (2, NULL);
