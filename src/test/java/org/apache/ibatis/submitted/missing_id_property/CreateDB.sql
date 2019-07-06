

drop table car if exists;
drop table part if exists;

create table car (
  car_id int,
  name varchar(20)
);

create table part (
  part_id int,
  name varchar(20),
  car_id int
);

insert into car (car_id, name) values(1, 'Audi');
insert into car (car_id, name) values(2, 'Ford');
insert into car (car_id, name) values(3, 'Fiat');

insert into part (part_id, name, car_id) values(100, 'door', 1);
insert into part (part_id, name, car_id) values(101, 'windshield', 1);
insert into part (part_id, name, car_id) values(101, 'brakes', 1);
