package org.apache.goat.chapter100.G.G021;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;

import java.io.Reader;

/**
 * Created by Administrator on 2020/2/7.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/2/7---14:34
 */
public class BaseTest {

  public static SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/goat/chapter100/G/G021/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/goat/chapter100/G/G021/CreateDB.sql");
  }
}
