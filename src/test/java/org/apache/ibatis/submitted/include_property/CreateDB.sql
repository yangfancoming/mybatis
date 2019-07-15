
# drop table if exists table1
drop table table1 if exists;

create table table1 (
  id int,
  a varchar(20),
  col_a varchar(20),
  col_b varchar(20),
  col_c varchar(20)
);

insert into table1 (id, a, col_a, col_b, col_c) values(1, 'a value', 'col_a value', 'col_b value', 'col_c value');
