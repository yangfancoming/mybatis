package org.apache.goat.chapter100.A.A010;

import org.apache.goat.MyBaseDataTest;
import org.junit.jupiter.api.Test;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A010/mybatis-config.xml";

  /** javaBean中的属性 与 数据库表字段 不对应情况的 两种解决方式：  */


  /**  解决方式一： 开启 全局 mapUnderscoreToCamelCase  配置
   * 运行结果可以看到 Employee{id=1, lastName='tom', email='tom@qq.com', gender='0'}
   * 即 开启了  <setting name="mapUnderscoreToCamelCase" value="true"/>  全局配置
   * 导致 javabean中的属性 lastName  数据库表中的字段 last_name 则 last_name 会被映射为 lastName 可以获取到该属性
  */
  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH);
//    Employee o = sqlSession.selectOne("com.goat.test.namespace.getEmpById", 1);
//    System.out.println(o.toString());
  }


  /** 解决方式二： 局部xml中的sql 查询字典使用as映射
   * 注释掉 全局 setting 配置 则 ( 解决方式一 运行结果 lastName 属性为null )
   * 运行结果仍然 可以看到  Employee{id=1, lastName='tom', email='tom@qq.com', gender='0'}
  */
  @Test
  void InputStream() throws Exception {
    setUpByInputStream(XMLPATH);
//    Employee o = sqlSession.selectOne("com.goat.test.namespace.getEmpById2", 1);
//    System.out.println(o.toString());
  }

}
