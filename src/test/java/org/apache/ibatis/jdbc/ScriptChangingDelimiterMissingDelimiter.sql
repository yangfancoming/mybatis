

drop table signon;

--//@DELIMITER /
create table signon (
username varchar(25) not null,
password varchar(25)  not null,
constraint pk_signon primary key (username)
);
--//@DELIMITER ;
