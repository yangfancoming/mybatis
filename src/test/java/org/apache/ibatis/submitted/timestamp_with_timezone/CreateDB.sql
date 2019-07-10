

drop table records if exists;

create table records (
  id int,
  odt timestamp with time zone
);

insert into records (id, odt) values
(1, '2018-01-02 11:22:33.123456000+01:23');
