

drop table users if exists;
drop table users2 if exists;

create table users (
  id int,
  name varchar(20),
  funkyNumber decimal(38),
  roundingMode int
);

insert into users (id, name, funkyNumber, roundingMode) 
values(1, 'User1', 123456789.9876543212345678987654321, 0);


create table users2 (
  id int,
  name varchar(20),
  funkyNumber decimal(38),
  roundingMode varchar(12)
);

insert into users2 (id, name, funkyNumber, roundingMode) 
values(1, 'User1', 123456789.9876543212345678987654321, 'UP');

