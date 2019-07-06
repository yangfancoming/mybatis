

drop table users if exists;
drop table product if exists;

create table users (
  id int,
  name varchar(20),
  city varchar(20),
  state char(20)
);

create table product (
  id int identity,
  name varchar(20)
);

insert into users (id, name, city, state) values(1, '   User1', '  Carmel  ', '  IN ');

insert into product (id, name) values
(1, 'iPod'),
(2, 'iPad');
