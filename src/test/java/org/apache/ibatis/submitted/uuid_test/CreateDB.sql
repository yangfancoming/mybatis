

drop table users if exists;

create table users (
  id varchar(36),
  name varchar(20)
);

insert into users (id, name) values('38400000-8cf0-11bd-b23e-10b96e4ef00d', 'User1');
