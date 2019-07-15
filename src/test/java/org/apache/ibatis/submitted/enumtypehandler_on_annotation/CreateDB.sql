

create table person (
  id int,
  firstName varchar(100),
  lastName varchar(100),
  personType int -- important: Enum original number (starting from 0)
);

INSERT INTO person (id, firstName, lastName, personType) VALUES (1, 'John', 'Smith', 0);
INSERT INTO person (id, firstName, lastName, personType) VALUES (2, 'Mike', 'Jordan', 1);
