

drop table users if exists;

create table users (
  id int,
  name varchar(20) ,
  dept_id int
);

insert into users (id, name,dept_id) values(1, 'User1',1);

drop table depts if exists;

create table depts (
  id int,
  name varchar(20)
);

insert into depts (id, name) values(1, 'Dept1');

