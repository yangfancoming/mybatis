

drop table records if exists;

create table records (
  id int,
  t time(9)
);

insert into records (id, t) values
(1, '11:22:33.123456789');
