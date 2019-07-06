

drop table person if exists;
drop table country if exists;

create table person(
	id int IDENTITY,
	name varchar(20)
);

create table country(
	id bigint IDENTITY,
	name varchar(20)
);

insert into person (id, name) values (1, 'Jane'), (2, 'John'); 
insert into country (id, name) values (1, 'Japan'), (2, 'New Zealand'); 
