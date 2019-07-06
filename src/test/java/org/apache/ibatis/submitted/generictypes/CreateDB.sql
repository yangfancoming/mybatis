

drop table users if exists;

create table groups (
id int,
owner varchar(16),
members varchar(256)
);

insert into groups values(1, 'Pocoyo', 'User1,User2,User3');
insert into groups values(2, 'Valentina', 'User1,User2,User3');
