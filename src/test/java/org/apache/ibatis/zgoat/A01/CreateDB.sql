


drop table foo if exists;

create table foo(
                     id int,
                     firstname varchar(20),
                     last_name varchar(20)
);

insert into foo(id, firstname, last_name) values (1, '111', 'Doe');
insert into foo(id, firstname, last_name) values (2, '222', 'Smith');


