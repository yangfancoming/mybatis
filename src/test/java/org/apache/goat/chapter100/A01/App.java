package org.apache.goat.chapter100.A01;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A01/mybatis-config.xml";

  /**
   * 运行结果可以看到 Employee{id=1, lastName='tom', email='tom@qq.com', gender='0'}
   * 即 开启了  <setting name="mapUnderscoreToCamelCase" value="true"/>  全局配置
   * 导致 javabean中的属性 lastName  数据库表中的字段 last_name 则 last_name 会被映射为 lastName 可以获取到该属性
  */
  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH);
    Employee o = sqlSession.selectOne("com.goat.test.namespace.getEmpById", 1);
    System.out.println(o.toString());
  }




}
