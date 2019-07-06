

drop table users if exists;
drop table users2 if exists;

create table users (
id int,
name varchar(16)
);

create table users2 (
id int IDENTITY,
name varchar(16)
);
