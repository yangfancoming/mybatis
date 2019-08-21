


drop table tbl_employee if exists;

create table tbl_employee(
                     id int(11) primary key auto_increment,
                     last_name varchar(255),
                     gender char(1),
                     email varchar(255)

);

insert into tbl_employee(id, firstname, last_name) values (1, 'tom', 0,'tom@qq.com');
insert into tbl_employee(id, firstname, last_name) values (2, 'jane', 1,'jane@qq.com');



