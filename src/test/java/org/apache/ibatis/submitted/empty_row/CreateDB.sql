

drop table parent if exists;
drop table child if exists;
drop table pet if exists;

create table parent (
id int,
col1 varchar(20),
col2 varchar(20)
);

create table child (
id int,
parent_id int,
name varchar(20)
);

create table pet (
id int,
parent_id int,
name varchar(20)
);

insert into parent (id, col1, col2) values
(1, null, null),
(2, null, null);

insert into child (id, parent_id, name) values
(1, 2, 'john'),
(2, 2, 'mary');
