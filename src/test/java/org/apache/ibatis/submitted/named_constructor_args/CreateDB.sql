

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  team int
);

insert into users (id, name, team) values
(1, 'User1', 99);
