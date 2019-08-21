package org.apache.goat.chapter100.A01;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.InputStream;


public class App {

  @Test
  public void tset() throws Exception {
    String resource = "org/apache/goat/chapter100/A01/mybatis-config.xml";
    // MyBatis 提供的工具类 Resources 加载配置文件，得到一个输入流
    try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
      //然后再通过 SqlSessionFactoryBuilder 对象的 build 方法 根据配置文件构建 SqlSessionFactory 对象
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      try (SqlSession session = sqlSessionFactory.openSession()) {

      }
    }
  }
}
