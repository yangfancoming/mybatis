
package org.apache.ibatis.autoconstructor;

import java.util.Date;

/**
 int,String,int,int,int,boolean,Date

 CREATE TABLE subject (
   id     INT NOT NULL,
   name   VARCHAR(20),
   age    INT NOT NULL,
   height INT,
   weight INT,
   active BIT,
   dt     TIMESTAMP
 );
 注意 CreateDB.sql 中 subject 表中 第4/5列属性为null  导致 运行后报错：
 Error instantiating class org.apache.ibatis.autoconstructor.PrimitiveSubject with invalid types
 (int,String,int,int,int,boolean,Date) or values (2,b,10,null,45,true,Tue Jul 09 15:18:28 CST 2019)
 解决方法：
 1. 基本/原始类型 int 不支持null   将 int 换成 Integer
 2. 使用 @AutomapConstructor 注解
*/
public class PrimitiveSubject {

  private final int id;
  private final String name;
  private final int age;
  private final int height;  // null
  private final int weight;   // null
  private final boolean active;
  private final Date dt;

  public PrimitiveSubject(final int id, final String name, final int age, final int height, final int weight, final boolean active, final Date dt) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.height = height;
    this.weight = weight;
    this.active = active;
    this.dt = dt;
  }
}
