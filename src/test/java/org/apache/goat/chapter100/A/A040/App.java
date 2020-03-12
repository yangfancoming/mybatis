package org.apache.goat.chapter100.A.A040;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A040/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /** <mappers>    注册方式一
   * 第一种方式 ：（resourc）通过resource指定   <mapper resource="com/dy/dao/userDao.xml"/>
   *
   * mapper:注册一个sql映射 局部配置文件
   * 				  resource：引用类路径下的sql映射文件
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }

}
