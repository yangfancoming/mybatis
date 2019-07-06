

drop table user_profile if exists;
drop table user_account if exists;

create table user_profile (
  id int,
  name varchar(20)
);

create table user_account (
  id int,
  name varchar(20)
);

insert into user_profile (id, name) values(1, 'Profile1');
insert into user_account (id, name) values(1, 'Account1');
