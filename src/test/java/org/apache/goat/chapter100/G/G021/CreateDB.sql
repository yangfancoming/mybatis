




# drop table foo if exists;
drop table if exists foo;
create table foo(
                     id int,
                     firstname varchar(20),
                     lastname varchar(20)
);

insert into foo(id, firstname, lastname) values (1, 'Jane', 'Doe');
insert into foo(id, firstname, lastname) values (2, 'John', 'Smith');
