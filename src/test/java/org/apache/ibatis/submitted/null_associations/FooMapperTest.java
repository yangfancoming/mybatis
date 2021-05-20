
package org.apache.ibatis.submitted.null_associations;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

class FooMapperTest {

  private final static String SQL_MAP_CONFIG = "org/apache/ibatis/submitted/null_associations/sqlmap.xml";
  private static SqlSession session;
  private static Connection conn;
  private static  FooMapper mapper;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    final SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(SQL_MAP_CONFIG));
    session = factory.openSession();
    conn = session.getConnection();
    mapper = session.getMapper(FooMapper.class);
    BaseDataTest.runScript(factory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/null_associations/create-schema-mysql.sql");

  }

  @BeforeEach
  void setUp() {
    mapper.deleteAllFoo();
    session.commit();
  }

  @Test
  void testNullAssociation() {
    final Foo foo = new Foo(1L, null, true);
    mapper.insertFoo(foo);
    session.commit();
    final Foo read = mapper.selectFoo();
    Assertions.assertEquals(1L, read.getField1(), "Invalid mapping");
    Assertions.assertNull(read.getField2(), "Invalid mapping - field2 (Bar) should be null");
    Assertions.assertTrue(read.isField3(), "Invalid mapping");
  }

  @Test
  void testNotNullAssociation() {
    final Bar bar = new Bar(1L, 2L, 3L);
    final Foo foo = new Foo(1L, bar, true);
    mapper.insertFoo(foo);
    session.commit();
    final Foo read = mapper.selectFoo();
    Assertions.assertEquals(1L, read.getField1(), "Invalid mapping");
    Assertions.assertNotNull(read.getField2(), "Bar should be not null");
    Assertions.assertTrue(read.isField3(), "Invalid mapping");
    Assertions.assertEquals(1L, read.getField2().getField1(), "Invalid mapping");
    Assertions.assertEquals(2L, read.getField2().getField2(), "Invalid mapping");
    Assertions.assertEquals(3L, read.getField2().getField3(), "Invalid mapping");
  }

  @AfterAll
  static void tearDownAfterClass() throws SQLException {
    conn.close();
    session.close();
  }
}
