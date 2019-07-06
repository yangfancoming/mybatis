

drop table users if exists;
drop table groups if exists;
drop table categories if exists;

create table users (
  id int,
  name varchar(20),
  owner_id int
);

create table groups (
  id int,
  name varchar(20)
);

insert into groups  (id, name) values(1, 'Group1');
insert into users (id, name, owner_id) values(1, 'User1', 1);
