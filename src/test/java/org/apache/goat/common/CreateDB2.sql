

drop table TBL_EMPLOYEE if exists;
create table TBL_EMPLOYEE(
                           id int(11) primary key auto_increment,
                           last_name varchar(255),
                           gender char(1),
                           email varchar(255)

);

insert into TBL_EMPLOYEE(id, last_name, gender, email) values (1, 'tom', 0,'tom@qq.com');
insert into TBL_EMPLOYEE(id, last_name, gender, email) values (2, 'jane', 1,'jane@qq.com');
