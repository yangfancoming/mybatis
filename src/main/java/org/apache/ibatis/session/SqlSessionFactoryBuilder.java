
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * Builds {@link SqlSession} instances.
 *
 * SqlSessionFactoryBuilder 类有很多的构造方法，但主要分为三大类：
 * 1、通过读取字符流（Reader）的方式构件SqlSessionFactory。
 * 2、通过字节流（InputStream）的方式构件SqlSessionFacotry。
 * 3、通过Configuration对象构建SqlSessionFactory。
 * 第1、2种方式是通过配置文件方式，第3种是通过Java代码方式。
 * build方法返回SqlSessionFactory接口的实现对象DefaultSqlSessionFactory。
 */
public class SqlSessionFactoryBuilder {

  /* Reader 相关处理 */
  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      // 创建全局配置文件解析器
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);// reader , null , null
      // 调用 parse 方法解析配置文件，生成 Configuration 对象
      Configuration configuration = parser.parse();
      SqlSessionFactory build = build(configuration);
      return build;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /* Reader 相关处理 */
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }


  /** XMLConfigBuilder#parse 方法是配置解析的主要方法
   我们可以看到SqlSessionFactoryBuilder 通过XMLConfigBuilder 去解析我们传入的mybatis的配置文件，
   构造出Configuration，最终返回new DefaultSqlSessionFactory(config)的SqlSessionFactory实例
  */
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      //1.构建XMLConfigBuilder对象
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      // 调用 parse 方法解析配置文件，生成 Configuration 对象
      Configuration configuration = parser.parse();
      /**
       创建 DefaultSqlSessionFactory
       2.建造者模式，XMLConfigBuilder对象的parse方法就可以构造Configuration对象，屏蔽了所有实现细节，并且将返回的Configuration对象作为参数构
       造SqlSessionFactory对象（SqlSessionFactory的默认实现类DefaultSqlSessionFactory），DefaultSqlSessionFactory拿到了配置对象之后，就具备生产SqlSession的能力了
      */
      SqlSessionFactory build = build(configuration);
      return build;
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /* Configuration 相关处理 */
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
