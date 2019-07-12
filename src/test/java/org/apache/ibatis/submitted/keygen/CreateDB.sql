


drop table if exists country;
drop table if exists planet;


CREATE TABLE country (
                       id int auto_increment primary key ,
                       countryname varchar(255) DEFAULT NULL,
                       countrycode varchar(255) DEFAULT NULL
);

CREATE TABLE planet (
                      id int auto_increment primary key ,
                      name varchar(32) DEFAULT NULL,
                      code varchar(64)
);


# drop table if exists country;
# drop table if exists planet;
#
#
# CREATE TABLE country (
#   id int IDENTITY,
#   countryname varchar(255) DEFAULT NULL,
#   countrycode varchar(255) DEFAULT NULL
# );
#
# CREATE TABLE planet (
#   id int IDENTITY,
#   name varchar(32) DEFAULT NULL,
#   code varchar(64) GENERATED ALWAYS AS (name || '-' || id)
# );



