

drop table permissions if exists;

create table permissions (
  resourceName varchar(20),
  principalName varchar(20),
  permission varchar(20)
);

insert into permissions values ('resource1', 'user1', 'read');
insert into permissions values ('resource1', 'user1', 'create');
insert into permissions values ('resource2', 'user1', 'delete');
insert into permissions values ('resource2', 'user1', 'update');
