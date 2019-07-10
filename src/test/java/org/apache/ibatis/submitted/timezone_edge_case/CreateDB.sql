

drop table records if exists;

create table records (
  id int,
  ts timestamp(9),
  d date
);

insert into records (id, ts, d) values
(1, '2019-03-10 02:30:00', '2011-12-30');
