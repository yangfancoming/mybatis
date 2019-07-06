

drop table users if exists;

create table users (
  id int,
  name varchar(128)
);

insert into users (id, name) values(1, 'User1');
