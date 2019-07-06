

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
insert into users values(1, 'User1', 2, 3);
insert into users values(2, 'User2', 1, 1);
insert into users values(2, 'User2', 1, 2);
insert into users values(2, 'User2', 1, 3);
insert into users values(3, 'User3', 1, 1);
insert into users values(3, 'User3', 2, 1);
insert into users values(3, 'User3', 3, 1);
insert into users values(4, 'User4', 1, 1);
insert into users values(4, 'User4', 1, 2);
insert into users values(4, 'User4', 2, 1);
insert into users values(4, 'User4', 2, 2);
