

drop table users if exists;

create table users (
  id int identity,
  name varchar(20),
  fld1 int,
  fld2 int
);

insert into users (id, name, fld1, fld2) values(1, 'User1', 12, 34);
