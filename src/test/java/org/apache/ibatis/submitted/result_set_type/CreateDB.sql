

drop table users if exists
go

drop procedure getusers if exists
go

create table users (
  id int,
  name varchar(20)
)
go

create procedure getusers()
modifies sql data
dynamic result sets 1
BEGIN ATOMIC
  declare cur cursor for select * from users order by id;
  open cur;
END
go

insert into users (id, name) values(1, 'User1')
go

insert into users (id, name) values(2, 'User1')
go

insert into users (id, name) values(3, 'User1')
go

insert into users (id, name) values(4, 'User1')
go

insert into users (id, name) values(5, 'User1')
go



