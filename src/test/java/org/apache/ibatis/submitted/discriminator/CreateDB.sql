

drop table vehicle if exists;
drop table owner if exists;

create table vehicle (
  id int,
  maker varchar(20),
  vehicle_type int,
  door_count int,
  carrying_capacity float
);

insert into vehicle (id, maker, vehicle_type, door_count, carrying_capacity) values
(1, 'Maker1', 1, 5, null),
(2, 'Maker2', 2, null, 1.5);

create table owner (
	id int,
	name varchar(20),
	vehicle_type varchar(20),
	vehicle_id int
);

insert into owner (id, name, vehicle_type, vehicle_id) values
(1, 'Owner1', 'truck', 2),
(2, 'Owner2', 'car', 1);
