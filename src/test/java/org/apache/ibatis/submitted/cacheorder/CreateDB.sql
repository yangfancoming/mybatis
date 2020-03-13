

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  username varchar(20),
  password varchar(20)
);

insert into users (id, name,username,password) values(1, 'User1','gaga','123');
