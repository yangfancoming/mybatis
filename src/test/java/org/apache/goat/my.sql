create table bar
(
  id        int         null,
  firstname varchar(20) null,
  lastname  varchar(20) null
);

create table country
(
  id          int auto_increment
    primary key,
  countryname varchar(255) null,
  countrycode varchar(255) null
);

create table foo
(
  id        int         null,
  firstname varchar(20) null,
  lastname  varchar(20) null
);

create table node
(
  id        int not null
    primary key,
  parent_id int null
);

create table param_test
(
  id   varchar(255) not null
    primary key,
  size bigint       null
);

create table person
(
  id        int         null,
  firstname varchar(20) null,
  lastname  varchar(20) null
);

create table planet
(
  id   int auto_increment
    primary key,
  name varchar(32) null,
  code varchar(64) null
);

create table post_tag
(
  post_id int not null,
  tag_id  int not null,
  primary key (post_id, tag_id)
);

create table product
(
  id   int auto_increment
    primary key,
  name varchar(20) null
);

create table table3
(
  id   int         not null,
  name varchar(20) null
);

create table tbl_dept
(
  id        int auto_increment
    primary key,
  dept_name varchar(255) not null
);

create table tbl_employee
(
  id        int auto_increment
    primary key,
  last_name varchar(255) null,
  gender    char         null,
  email     varchar(255) null,
  d_id      int          null
);

create table users
(
  id    int         null,
  name  varchar(20) null,
  city  varchar(20) null,
  state char(20)    null
);

create table what
(
  id        int auto_increment
    primary key,
  last_name varchar(255) null,
  gender_   char         null,
  email_    varchar(255) null
);

create table zoo
(
  id        int         null,
  firstname varchar(20) null,
  last_name varchar(20) null
);

