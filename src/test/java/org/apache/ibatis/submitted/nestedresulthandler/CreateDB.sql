

drop table persons if exists;
drop table items if exists;

create table persons (
  id int,
  name varchar(20)
);

create table items (
  id int,
  owner int,
  name varchar(20)
);
 
insert into persons (id, name) values (1, 'grandma');
insert into persons (id, name) values (2, 'sister');
insert into persons (id, name) values (3, 'brother');

insert into items (id, owner, name) values (1, 1, 'book');
insert into items (id, owner, name) values (2, 1, 'tv');
insert into items (id, owner, name) values (3, 2, 'shoes');
insert into items (id, owner, name) values (4, 3, 'car');
insert into items (id, owner, name) values (5, 2, 'phone');
