

drop table articles if exists;
drop table authors if exists;

create table articles (
  id int,
  name varchar(20),
  author_id int,
  coauthor_id int
);

create table authors (
  id int,
  name varchar(20)
);

insert into articles (id, name, author_id, coauthor_id) values
(1, 'Article 1', 1, 2),
(2, 'Article 2', 3, 1);

insert into authors (id, name) values
(1, 'Mary'), (2, 'Bob'), (3, 'Jane');
