package org.apache.goat.chapter100.A.A036;

import org.apache.common.MyBaseDataTest;
import org.apache.goat.common.model.Foo;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.junit.jupiter.api.Test;

/**
 * 源码位置： <settings> 标签解析
 * @see XMLConfigBuilder#environmentsElement(org.apache.ibatis.parsing.XNode)
 */
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A036/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";

  /**
   * 切换   注释一下两个标签  查看运行结果
   * <environments default="pro_mysql">   连接 myslq   数据库 查询   id=1, lastname=Doe, firstname=Jane
   * <environments default="dev_hsqldb">  连接 hsqldb  数据库 查询   id=1, lastname=null, firstname=111
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }
}
