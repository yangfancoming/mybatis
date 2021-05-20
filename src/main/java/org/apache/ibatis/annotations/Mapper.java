
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * Marker interface for MyBatis mappers
 * 使用该注解时只需要在对应的mapper的接口上添加注解@Mapper即可让spring加载这个Bean，且根据注解定义的sql语句实现功能。
 * 不过这个注解的加载不是在mybatis的主jar包中，而是在这个jar中找到了这个方法：
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface Mapper {
  // Interface Mapper
}
