

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  state int
);

insert into users (id, name, state) values(1, 'Inactive', 0);
