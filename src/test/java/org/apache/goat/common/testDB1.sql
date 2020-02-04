


drop table p_user if exists;

create table p_user(
                     id int primary key auto_increment,
                     name varchar(10),
                     sex char(2)
);

insert into p_user(name,sex) values('A',"男");
insert into p_user(name,sex) values('B',"女");
insert into p_user(name,sex) values('C',"男");


#创建存储过程(查询得到男性或女性的数量, 如果传入的是0就女性否则是男性)
DELIMITER $
CREATE PROCEDURE GES_USER_COUNT(IN sex_id INT, OUT user_count INT)
BEGIN
  IF sex_id=0 THEN
    SELECT COUNT(*) FROM p_user WHERE p_user.sex='女' INTO user_count;
  ELSE
    SELECT COUNT(*) FROM p_user WHERE p_user.sex='男' INTO user_count;
  END IF;
END
$

#调用存储过程
DELIMITER ;
SET @user_count = 0;
CALL GES_USER_COUNT(1, @user_count);
SELECT @user_count;
