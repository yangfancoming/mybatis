package org.apache.goat.chapter100.Z000;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Employee;
import org.junit.jupiter.api.Test;


public class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/Z000/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB2.sql";


  //  这里为啥报错：user lacks privilege or object not found: WHAT in statement [select * from what where id = ?]  折磨死我了。。。
  // sos 是由于 CreateDB2.sql  文件中的sql语句错误 导致 hsqldb 数据库 报错  注意：hsqldb数据库的主键定义和自动增长的定义方式 和 mysql 是不一样的
  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Employee o = sqlSession.selectOne("com.goat.test.namespace.getEmpById", 1);
    System.out.println(o.toString());
  }




}
