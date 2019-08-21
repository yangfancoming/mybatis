package org.apache.goat.chapter100.Z000;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/Z000/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";


  // doit 这里为啥报错：user lacks privilege or object not found: WHAT in statement [select * from what where id = ?]  折磨死我了。。。
  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Employee o = sqlSession.selectOne("com.goat.test.namespace.getEmpById", 1);
    System.out.println(o.toString());
  }




}
