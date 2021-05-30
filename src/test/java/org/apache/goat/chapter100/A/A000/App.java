package org.apache.goat.chapter100.A.A000;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

/**
 # mybatis  读取 xml 的三种方式
 2020年1月1日20:34:52
 * SqlSessionFactoryBuilder 类有很多的构造方法，但主要分为三大类：
 * 1、通过读取字符流（Reader）的方式构件SqlSessionFactory。
 * 2、通过字节流（InputStream）的方式构件SqlSessionFacotry。
 * 3、通过Configuration对象构建SqlSessionFactory。
 * 第1、2种方式是通过配置文件方式，第3种是通过Java代码方式。
*/
class App extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/goat/chapter100/A/A000/mybatis-config.xml";

  /**  1、通过读取字符流（Reader）的方式构件SqlSessionFactory */
  @Test
  void Reader() throws Exception {
    setUpByReader(XMLPATH);
    common();
  }

  /**  2、通过字节流（InputStream）的方式构件SqlSessionFacotry */
  @Test
  void InputStream() throws Exception {
    setUpByInputStream(XMLPATH);
    common();
  }

  void common(){
    Configuration configuration = sqlSessionFactory.getConfiguration();
    Environment environment = configuration.getEnvironment();
    DataSource dataSource = environment.getDataSource();
    // 验证全局xml文件配置
    Assert.assertTrue(dataSource instanceof UnpooledDataSource);
    Assert.assertEquals("development",environment.getId());
  }
}
