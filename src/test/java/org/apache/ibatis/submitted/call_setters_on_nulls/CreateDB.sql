

drop table users if exists;

drop table users2 if exists;

create table users (
  id int,
  name varchar(20)
);

create table users2(
  id int,
  name varchar(20)
)

insert into users (id, name) values(1, NULL);

insert into users2 (id, name) values( 2, 'mary' );
insert into users2 (id, name) values( 1, NULL );


