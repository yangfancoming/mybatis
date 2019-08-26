package org.apache.goat.chapter100.A022;

import org.apache.goat.MyBaseDataTest;
import org.apache.goat.common.Foo;
import org.junit.jupiter.api.Test;


class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A022/mybatis-config.xml";
  public static final String DBSQL = "org/apache/goat/common/CreateDB.sql";


  /**
   *  <properties> 标签从外部配置文件读取属性值
   * 读取的是  resources/dbconfig.properties 路径下 属性文件
   *
   * 如果属性在不只一个地方进行了配置，那么 MyBatis 将按照下面的顺序来加载：
   * 1.在 properties 元素体内指定的属性首先被读取。
   * 2.然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性。
   * 3.最后读取作为方法参数传递的属性，并覆盖已读取的同名属性。
   * 因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的是 properties 属性中指定的属性。
  */
  @Test
  void test1() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Foo foo = sqlSession.selectOne("com.goat.test.namespace.selectById",1);
    System.out.println(foo);
  }



}
