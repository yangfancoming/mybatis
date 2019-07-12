
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.StatementType;

/** @SelectKey 通过 SQL 语句获得主键的注解；
 用于在sql语句执行前（后）插入执行，如在执行前（后）查询当前自增张id的值，注解中声明的属性为：

 （1）sql语句执行后
 @Update("update table2 set name = #{name} where id = #{nameId}")
 在更新语句后执行，查询nameId对应的name_fred，并赋值给Name类的generatedName属性（入参name的generatedName属性）
 @SelectKey(statement="select name_fred from table2 where id = #{nameId}",keyProperty="generatedName", keyColumn="NAME_FRED", before=false, resultType=String.class)
 int updateTable2WithSelectKeyWithKeyMap(Name name);

 （2）sql语句执行前
 @Insert("insert into table3 (id, name) values(#{nameId}, #{name})")
 在插入语句执行之前，查询数据库表中下一个id的值，并赋值给Name对象name的nameId属性
 @SelectKey(statement="call next value for TestSequence", keyProperty="nameId", before=true, resultType=int.class)
 int insertTable3(Name name);
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelectKey {
  String[] statement();  //sql子句

  String keyProperty();//属性名，将查询得到列中的数据赋值属性名对应的属性

  String keyColumn() default ""; //查询返回的列名

  boolean before(); //表示在是否在主sql执行前执行

  Class<?> resultType(); //返回的值的类型

  StatementType statementType() default StatementType.PREPARED; //状态
}
