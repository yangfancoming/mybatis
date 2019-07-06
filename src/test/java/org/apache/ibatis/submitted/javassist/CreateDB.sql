

drop table users if exists;
drop table groups if exists;

create table users (
  id int,
  name varchar(20)
);
create table groups (
  id int,
  owner int,
  name varchar(20)
);

insert into users (id, name) values(1, 'User1');
insert into groups (id, owner, name) values(1, 1, 'Group1');
