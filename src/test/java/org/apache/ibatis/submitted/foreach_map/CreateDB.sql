

drop table string_string if exists;
create table string_string (
    id identity,
    key varchar(255),
    value varchar(255)
);



drop table int_bool if exists;
create table int_bool (
    id identity,
    key integer,
    value boolean
);

drop table nested_bean if exists;
create table nested_bean (
    id identity,
    keya integer,
    keyb boolean,
    valuea integer,
    valueb boolean
);


drop table key_cols if exists;
create table key_cols (
    id identity,
    col_a integer,
    col_b integer
);

insert into key_cols (id, col_a, col_b) values (1, 11, 222);
insert into key_cols (id, col_a, col_b) values (2, 22, 222);
insert into key_cols (id, col_a, col_b) values (3, 22, 333);
