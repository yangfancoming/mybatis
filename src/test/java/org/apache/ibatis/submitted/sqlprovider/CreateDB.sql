

drop table users if exists;
drop table memos if exists;

create table users (
  id int,
  name varchar(20),
  logical_delete boolean default false
);

create table memos (
   id int,
   memo varchar(1024),
);

insert into users (id, name) values(1, 'User1');
insert into users (id, name) values(2, 'User2');
insert into users (id, name) values(3, 'User3');
insert into users (id, name, logical_delete) values(4, 'User4', true);

insert into memos (id, memo) values(1, 'memo1');
