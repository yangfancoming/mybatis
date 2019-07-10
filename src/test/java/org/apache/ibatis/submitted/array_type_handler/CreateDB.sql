

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  nicknames varchar(20) array
);
