
package org.apache.goat;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;

import java.io.InputStream;
import java.io.Reader;

public abstract class MyBaseDataTest {


  // 由于 SqlSession 和 connection 一样都是非现场安全的  因此不能当做成员变量 此处只是用作 学习 测试之用
  public static SqlSession sqlSession;

  public static SqlSessionFactory sqlSessionFactory;


  /** Reader 使用内存数据库 */
  public void setUpByReader(String xmlPath,String dbSql) throws Exception {
    try (Reader reader = Resources.getResourceAsReader(xmlPath)) {
      // 通过 mybatis 全局配置文件  创建  sqlSessionFactory
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      // 通过 sqlSessionFactory 创建 sqlSession
      sqlSession = sqlSessionFactory.openSession(false);
    }
    // 创建内存数据库 并添加插入测试数据
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), dbSql);
  }

  /** InputStrea 使用内存数据库 */
  public void setUpByInputStream(String xmlPath,String dbSql) throws Exception {
    try (InputStream inputStream = Resources.getResourceAsStream(xmlPath)) {
      //然后再通过 SqlSessionFactoryBuilder 对象的 build 方法 根据配置文件构建 SqlSessionFactory 对象
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      sqlSession = sqlSessionFactory.openSession(false);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), dbSql);
  }


  /** Reader 使用真实数据库 */
  public void setUpByReader(String xmlPath) throws Exception {
    try (Reader reader = Resources.getResourceAsReader(xmlPath)) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSession = sqlSessionFactory.openSession(false);
    }
  }


  /** InputStrea 使用真实数据库 */
  public void setUpByInputStream(String xmlPath) throws Exception {
    try (InputStream inputStream = Resources.getResourceAsStream(xmlPath)) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      sqlSession = sqlSessionFactory.openSession(false);
    }
  }




  @AfterEach
  public void after(){
    sqlSession.close();
    System.out.println("此次测试运行结束，关闭 sqlSession ");
  }


}
