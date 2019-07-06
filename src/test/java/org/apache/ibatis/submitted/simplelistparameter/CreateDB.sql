

drop table car if exists;

create table car (
  name varchar(20),
  doors int
);

insert into car (name, doors) values('Audi', 4);
insert into car (name, doors) values('Ford', 4);
insert into car (name, doors) values('Fiat', 4);
