

drop table if exists person;
create table person (id int, nr_department int);

insert into person (id,  nr_department) 
values (1, 1);

drop table if exists productattribute;
create table productattribute (nr_id int);

insert into productattribute(nr_id) 
values (1);

drop table if exists department;
create table department (nr_id int,nr_attribute int,person int);

insert into department(nr_id,nr_attribute,person) 
values (1,1,1);

