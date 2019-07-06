

drop table a if exists;
drop table b if exists;

create table a (
  id int,
  name varchar(20)
);

create table b (
  ref int,
  entry int
);

insert into a (id, name) values(0, 'some');
insert into a (id, name) values(1, 'other');

insert into b (ref,entry) values (0, 1);
insert into b (ref,entry) values (1, 2);
insert into b (ref,entry) values (0, 3);

