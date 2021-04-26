
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
 * 建造者模式 构造者模式  优点：对复杂对象的创建细节隐藏，使用者直接调用方法即可拿到对象。
 * SqlSessionFactoryBuilder 类有很多的构造方法，但主要分为三大类：
 * 1、通过读取字符流（Reader）的方式构件SqlSessionFactory。
 * 2、通过字节流（InputStream）的方式构件SqlSessionFacotry。
 * 3、通过Configuration对象构建SqlSessionFactory。
 * 第1、2种方式是通过配置文件方式，第3种是通过Java代码方式。
 * build方法返回SqlSessionFactory接口的实现对象DefaultSqlSessionFactory。
 */
public class SqlSessionFactoryBuilder {

  /* Reader 相关处理 =================================================================================================================================*/
  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  // mybatis配置文件 + properties, 此时mybatis配置文件中可以不配置 <properties> 标签 ，也能使用${}形式
  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  // 通过XMLConfigBuilder解析mybatis配置，然后创建SqlSessionFactory对象
  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      /**
       * 创建全局配置文件解析器 并且填充 XPathParser 类的两个属性
       *   XPathParser this.xpath = XPathFactory.newInstance().newXPath()
       *   XPathParser this.document = createDocument(new InputSource(reader));
      */
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);// reader , null , null
      // 调用 parse 方法解析配置文件，生成 Configuration 对象
      Configuration configuration = parser.parse();
      return build(configuration);
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

  /* Reader 相关处理 =================================================================================================================================*/
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  /**
   * 新建一个对mybatis的XML配置文件进行解析的解析器对应配置文件先进性解析封装
   * XMLConfigBuilder#parse 方法是配置解析的主要方法
   * 我们可以看到SqlSessionFactoryBuilder 通过XMLConfigBuilder 去解析我们传入的mybatis的配置文件，
   * 构造出Configuration，最终返回new DefaultSqlSessionFactory(config)的SqlSessionFactory实例
   * @param inputStream 配置文件文件流
   * @param environment 加载哪种环境(开发环境/生产环境)，包括数据源和事务管理器
   * @param properties 属性配置文件，那些属性可以用${propName}语法形式多次用在配置文件中
   * @return
   */
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      // 创建全局配置文件解析器  //新建一个对mybatis的XML配置文件进行解析的解析器
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      // 调用 parse 方法解析配置文件，生成 Configuration 对象 //parse：对Mybatis配置文件xml中的标签信息进行解析封装，然后添加到Mybatis全局配置信息中，返回Mybatis全局配置信息对象
      Configuration configuration = parser.parse();
      /**
       创建 DefaultSqlSessionFactory
       2.建造者模式，XMLConfigBuilder对象的parse方法就可以构造Configuration对象，屏蔽了所有实现细节，并且将返回的Configuration对象作为参数构
       造SqlSessionFactory对象（SqlSessionFactory的默认实现类DefaultSqlSessionFactory），DefaultSqlSessionFactory拿到了配置对象之后，就具备生产SqlSession的能力了
      */
      SqlSessionFactory build = build(configuration);
      return build;
    } catch (Exception e) {
      // 包装解析异常，进行更加具体的描述
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      // 重置异常上下文实例
      ErrorContext.instance().reset();
      try {
        // 关闭配置文件文件流
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /* 以上所有代码的最终调用 =================================================================================================================================*/

  // 通过 Configuration 构建出 SqlSessionFactory
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }
}
