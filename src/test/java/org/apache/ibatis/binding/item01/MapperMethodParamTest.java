
package org.apache.ibatis.binding.item01;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
映射方法参数
*/
class MapperMethodParamTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession session;
  private static Mapper mapper;

  @BeforeAll
  static void setup() throws Exception {
    DataSource dataSource = BaseDataTest.createUnpooledDataSource(BaseDataTest.BLOG_PROPERTIES);
    BaseDataTest.runScript(dataSource, "org/apache/ibatis/binding/item01/paramtest-schema.sql");
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("Production", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(Mapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    session = sqlSessionFactory.openSession(true);
    mapper = session.getMapper(Mapper.class);
  }

  @Test
  public void selectAll(){
    Map<String, Object> map = mapper.selectAll();
    System.out.println(map);
  }

  @Test
  public void selectSize2(){
    Long foo1 = mapper.selectSize1("foo");
    Long foo2 = mapper.selectSize2("foo");
    System.out.println(foo1);
    System.out.println(foo2);
  }

  @Test
  void parameterNameIsSizeAndTypeIsLong() {
    Long foo = mapper.selectSize1("foo");
    assertThat(foo).isEqualTo(Long.MAX_VALUE);
  }

  @Test
  void parameterNameIsSizeUsingHashMap() {
      HashMap<String, Object> params = new HashMap<>();
      params.put("id", "foo");
      params.put("size", Long.MAX_VALUE);
      mapper.insertUsingHashMap(params);
      assertThat(mapper.selectSize1("foo")).isEqualTo(Long.MAX_VALUE);
  }

}
