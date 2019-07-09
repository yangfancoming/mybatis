

drop table users if exists;

drop table books if exists;

create table users (
  id int,
  name varchar(20),
  phone varchar(20),
  phone_number bigint
);

create table books (
  version int,
  name varchar(20)
);

create table pets (
  id int,
  owner int,
  breeder int,
  name varchar(20)
);

create table breeder (
  id int,
  name varchar(20)
);

-- '+86 12345678901' can't be converted to a number
insert into users (id, name, phone, phone_number) values(1, 'User1', '+86 12345678901', 12345678901);
insert into users (id, name, phone, phone_number) values(2, 'User2', '+86 12345678902', 12345678902);

insert into books (version, name) values(99, 'Learn Java');

insert into pets (id, owner, breeder, name) values(11, 1, null, 'Ren');
insert into pets (id, owner, breeder, name) values(12, 2, 101, 'Chien');
insert into pets (id, owner, breeder, name) values(13, 2, null, 'Kotetsu');

insert into breeder (id, name) values(101, 'John');
