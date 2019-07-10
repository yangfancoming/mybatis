

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  cur varchar(20)
);

insert into users (id, name, cur) values(1, 'User1', 'RMB');
