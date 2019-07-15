

drop table users if exists;

create table users (
  uid int,
  name varchar(20)
);

insert into users (uid, name) values(1, 'User1');
insert into users (uid, name) values(2, 'User2');
