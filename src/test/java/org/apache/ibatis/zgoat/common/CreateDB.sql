


drop table foo if exists;

create table foo(
                     id int,
                     firstname varchar(20),
                     last_name varchar(20)
);

insert into foo(id, firstname, last_name) values (1, '111', 'Doe');
insert into foo(id, firstname, last_name) values (2, '222', 'Smith');


drop table bar if exists;

create table bar(
                  id int,
                  firstname varchar(20),
                  last_name varchar(20)
);

insert into bar(id, firstname, last_name) values (1, 'bar111', 'bar111');
insert into bar(id, firstname, last_name) values (2, 'bar222', 'bar222');


