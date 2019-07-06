

drop table users if exists;

create table users (
  id int,
  name varchar(20)
);

insert into users (id, name) values(1, 'User1');
insert into users (id, name) values(2, 'User2');
insert into users (id, name) values(3, 'User3');
