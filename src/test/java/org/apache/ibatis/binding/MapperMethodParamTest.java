
package org.apache.ibatis.binding;

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

class MapperMethodParamTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSession session;
  private static Mapper mapper;

  @BeforeAll
  static void setup() throws Exception {
    DataSource dataSource = BaseDataTest.createUnpooledDataSource(BaseDataTest.BLOG_PROPERTIES);
    BaseDataTest.runScript(dataSource, "org/apache/ibatis/binding/paramtest-schema.sql");
    TransactionFactory transactionFactory = new JdbcTransactionFactory();
    Environment environment = new Environment("Production", transactionFactory, dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(Mapper.class);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    session = sqlSessionFactory.openSession();
    mapper = session.getMapper(Mapper.class);
  }

  @Test
  void parameterNameIsSizeAndTypeIsLong() {
    mapper.insert("foo", Long.MAX_VALUE);
    assertThat(mapper.selectSize("foo")).isEqualTo(Long.MAX_VALUE);
    Map<String, Object> map = mapper.selectAll();
    System.out.println(map);
  }

  @Test
  void parameterNameIsSizeUsingHashMap() {
      HashMap<String, Object> params = new HashMap<>();
      params.put("id", "foo");
      params.put("size", Long.MAX_VALUE);
      mapper.insertUsingHashMap(params);
      assertThat(mapper.selectSize("foo")).isEqualTo(Long.MAX_VALUE);
  }


}
