
package org.apache.ibatis.binding.item01;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 映射方法参数
 * 源码搜索串：  paramNameResolver = new ParamNameResolver(configuration, method);
*/
class MapperMethodParamTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession session;
  private static Mapper mapper;

  @BeforeAll
  static void setup() throws Exception {
    DataSource dataSource = BaseDataTest.createUnpooledDataSource(BaseDataTest.BLOG_PROPERTIES);
    BaseDataTest.runScript(dataSource, "org/apache/ibatis/binding/item01/paramtest-schema.sql");
    Environment environment = new Environment("Production", new JdbcTransactionFactory(), dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(Mapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    session = sqlSessionFactory.openSession(true);
    mapper = session.getMapper(Mapper.class);
  }

  // 无参的情况
  @Test
  public void selectAll(){
    Map<String, Object> map = mapper.selectAll(); // doit 为什么会调用 selectOne？？？
    System.out.println(map);
  }

  @Test
  public void selectList(){
    List list = mapper.selectList();
    System.out.println(list.size());
  }

  @Test
  public void testInsert(){
//    System.out.println( mapper.insert("1",2));
//    Assert.assertEquals(Long.valueOf(2),mapper.selectSize1("1"));
  }


  //  走 @Param  注解的情况    Long selectSize1(@Param("id") String id);
  @Test
  public void parameterNameIsSizeAndTypeIsLong(){
    Assert.assertEquals(Long.valueOf(Long.MAX_VALUE),mapper.selectById1("foo"));
  }

  // 没有 @Param  注解的情况   Long selectSize2(String id);
  @Test
  public void test2(){
    Assert.assertEquals(Long.valueOf(Long.MAX_VALUE),mapper.selectById2("foo"));
  }

  @Test
  void parameterNameIsSizeUsingHashMap() {
    HashMap<String, Object> params = new HashMap<>();
    params.put("id", "foo");
    params.put("size", Long.MAX_VALUE);
    mapper.insertUsingHashMap(params);
    Assert.assertEquals(Long.valueOf(Long.MAX_VALUE),mapper.selectById1("foo"));
  }
}
