

drop table if exists users ;
create table users (
  id int,
  name varchar(20)
);
insert into users (id, name) values(1, NULL);



drop table if exists users2 ;
create table users2(
  id int,
  name varchar(20)
);
insert into users2 (id, name) values( 2, 'mary' );
insert into users2 (id, name) values( 1, NULL );


