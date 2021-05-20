
package org.apache.ibatis.submitted.awful_table;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AwfulTableTest {

  protected SqlSessionFactory sqlSessionFactory;

  @BeforeEach
  void setUp() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/awful_table/MapperConfig.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/awful_table/CreateDB.sql");
  }

  @Test
  void testAwfulTableInsert() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      AwfulTableMapper mapper = sqlSession.getMapper(AwfulTableMapper.class);
      AwfulTable record = new AwfulTable();
      record.seteMail("fred@fred.com");
      record.setEmailaddress("alsofred@fred.com");
      record.setFirstFirstName("fred1");
      record.setFrom("from field");
      record.setId1(1);
      record.setId2(2);
      record.setId5(5);
      record.setId6(6);
      record.setId7(7);
      record.setSecondFirstName("fred2");
      record.setThirdFirstName("fred3");

      mapper.insert(record);
      Integer generatedCustomerId = record.getCustomerId();
      assertEquals(57, generatedCustomerId.intValue());

      AwfulTable returnedRecord = mapper.selectByPrimaryKey(generatedCustomerId);

      assertEquals(generatedCustomerId, returnedRecord.getCustomerId());
      assertEquals(record.geteMail(), returnedRecord.geteMail());
      assertEquals(record.getEmailaddress(), returnedRecord.getEmailaddress());
      assertEquals(record.getFirstFirstName(), returnedRecord.getFirstFirstName());
      assertEquals(record.getFrom(), returnedRecord.getFrom());
      assertEquals(record.getId1(), returnedRecord.getId1());
      assertEquals(record.getId2(), returnedRecord.getId2());
      assertEquals(record.getId5(), returnedRecord.getId5());
      assertEquals(record.getId6(), returnedRecord.getId6());
      assertEquals(record.getId7(), returnedRecord.getId7());
      assertEquals(record.getSecondFirstName(), returnedRecord.getSecondFirstName());
      assertEquals(record.getThirdFirstName(), returnedRecord.getThirdFirstName());
    }
  }

}
