

drop table articles if exists;
drop table authors if exists;

create table articles (
  id int,
  title varchar(20),
  author_id int
);

create table authors (
  id int,
  name varchar(20)
);

insert into articles (id, title, author_id) values
(1, 'Article1', 100),
(2, 'Article2', 200);

insert into authors (id, name) values
(100, 'Author1'),
(200, 'Author2');
