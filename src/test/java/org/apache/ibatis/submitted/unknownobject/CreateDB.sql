

drop table users if exists;

create table users (
  id int,
  name varchar(20),
  unknownObject varchar(20)
);

insert into users (id, name, unknownObject) values(1, 'User1', 'unknownObject');
