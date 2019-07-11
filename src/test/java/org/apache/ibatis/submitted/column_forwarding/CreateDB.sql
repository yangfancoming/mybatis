

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  group_id int,
  state varchar(20)
);

insert into users (id, name, group_id, state) values (1, 'User1', 1, 'active');
insert into users (id, name, group_id, state) values (2, 'User2', null, null);


create table groups (
  id int,
  state varchar(20)
);

insert into groups (id, state) values (1, 'active');

