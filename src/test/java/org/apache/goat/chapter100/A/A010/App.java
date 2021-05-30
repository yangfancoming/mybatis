package org.apache.goat.chapter100.A.A010;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 解析全局xml文件 <settings>  标签
 * @see XMLConfigBuilder#settingsAsProperties(org.apache.ibatis.parsing.XNode)
 * 将解析后的配置信息保存到全局Configuration对象中
 * @see XMLConfigBuilder#settingsElement(java.util.Properties)
*/
public class App extends MyBaseDataTest {

  public static final String SETTINGS1 = "org/apache/goat/chapter100/A/A010/settings1.xml";
  public static final String SETTINGS2 = "org/apache/goat/chapter100/A/A010/settings2.xml";
  public static final String XMLPATH = "org/apache/goat/chapter100/A/A010/mybatis-config.xml";

  /**
   * if (!metaConfig.hasSetter(String.valueOf(key)))
   * 如果<settings>  标签中配置了 Configuration 对象中不存在的属性 那么则会报异常
  */
  @Test
  void test0() {
    Assertions.assertThrows(PersistenceException.class, ()->setUpByReader(XMLPATH));
  }

  /**
   * 测试全局<settings> 标签的 mapUnderscoreToCamelCase 和 cacheEnabled 属性解析
  */
  @Test
  void test1() throws Exception {
    // 根据指定路径解析配置文件 并创建全局Configuration对象
    SqlSessionFactory sqlSessionFactory = setUpByReaderNoOpen(SETTINGS1);
    // 获取全局Configuration对象 并验证
    Configuration configuration = sqlSessionFactory.getConfiguration();
    Assert.assertTrue(configuration.isMapUnderscoreToCamelCase());
    Assert.assertFalse(configuration.isCacheEnabled());
  }

  /**
   * @see Configuration#setDefaultExecutorType(org.apache.ibatis.session.ExecutorType)
   * 测试全局<settings>标签中的defaultExecutorType 的value 必须为枚举类  SIMPLE, REUSE, BATCH 中的一种否则会报异常
   * <setting name="defaultExecutorType" value="FUCK"/>
  */
  @Test
  void test2() {
    Assertions.assertThrows(PersistenceException.class, ()->setUpByReader(SETTINGS2));
  }

  /**
   * javaBean中的属性 与 数据库表字段 不对应情况的 两种解决方式：
   * 1.使用 mapUnderscoreToCamelCase 属性
   * javabean中的属性 lastName  数据库表中的字段 last_name 则 last_name 会被映射为 lastName 可以获取到该属性
   * 2. 局部xml中的sql 查询字段使用as映射
  */

}
