

drop table person if exists;

create table person(
	id int,
	firstname varchar(20),
	lastname varchar(20)
);

insert into person(id, firstname, lastname) values (1, 'Jane', 'Doe'); 
insert into person(id, firstname, lastname) values (2, 'John', 'Smith'); 
