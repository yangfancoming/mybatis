

# drop table person if exists;
drop table if exists person;
create table person(
	id int,
	firstname varchar(20),
	lastname varchar(20)
);

insert into person(id, firstname, lastname) values (1, 'Jane', 'Doe'); 
insert into person(id, firstname, lastname) values (2, 'John', 'Smith');

