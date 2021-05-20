
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 给指定的mapper接口增加sql语句实现查询功能，使用方法为：
 @Select("select * from users")
 List<User> getAllUsers();
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
  String[] value();
}
