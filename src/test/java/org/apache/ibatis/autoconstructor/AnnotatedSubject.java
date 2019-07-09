
package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.annotations.AutomapConstructor;

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
*/
public class AnnotatedSubject {
  private final int id;
  private final String name;
  private final int age;
  private final int height;
  private final int weight;



  public AnnotatedSubject(final int id, final String name, final int age, final int height, final int weight) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.height = height;
    this.weight = weight;
  }

  // 注意 CreateDB.sql 中 subject 表中 第4/5列属性为null
  @AutomapConstructor
  public AnnotatedSubject(final int id, final String name, final int age, final Integer height, final Integer weight) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.height = height == null ? 0 : height;
    this.weight = weight == null ? 0 : weight;
  }
}
