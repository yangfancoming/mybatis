

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  group_id int,
  rol_id int
);

insert into users values(1, 'User1', 1, 1);
insert into users values(1, 'User1', 1, 2);
insert into users values(1, 'User1', 2, 1);
insert into users values(1, 'User1', 2, 2);
