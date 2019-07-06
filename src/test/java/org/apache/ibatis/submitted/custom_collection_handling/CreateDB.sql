

drop table if exists contact;
drop table if exists person;
create table person (
    id int, 
    name varchar(32),
    primary key (id)
);

insert into person (id, name) values (1, 'John');
insert into person (id, name) values (2, 'Rebecca');

create table contact (
    id int, 
    address varchar(100), 
    phone varchar(32), 
    person_id int,
    foreign key (person_id) references person(id)
);

insert into contact (id, address, phone, person_id)
    values (1, '123 St. Devel', '555-555-555', 1);
insert into contact (id, address, phone, person_id)
    values (2, '3 Wall Street', '111-111-111', 1);
insert into contact (id, address, phone, person_id)
    values (3, 'White House', '000-999-888', 2);

