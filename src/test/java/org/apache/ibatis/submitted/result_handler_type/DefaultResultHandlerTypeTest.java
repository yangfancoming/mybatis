
package org.apache.ibatis.submitted.result_handler_type;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

class DefaultResultHandlerTypeTest {

  String xmlConfig = "org/apache/ibatis/submitted/result_handler_type/MapperConfig.xml";

  @Test
  void testSelectList() throws Exception {
    SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.result_handler_type.PersonMapper.doSelect");
      assertEquals(list.size(), 2);
        /**  doit 为啥 注释掉 ObjectFactory 和 	<objectFactory type="org.apache.ibatis.submitted.result_handler_type.ObjectFactory" />  后  报错：
         *       Expected :java.util.LinkedList
         *       Actual   :java.util.ArrayList
        */
      assertEquals("java.util.LinkedList", list.getClass().getCanonicalName());
    }
  }

  @Test
  void testSelectMap() throws Exception {
    SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Map<Integer, Person> map = sqlSession.selectMap("org.apache.ibatis.submitted.result_handler_type.PersonMapper.doSelect", "id");
      assertEquals(map.size(), 2);
      assertEquals("java.util.LinkedHashMap", map.getClass().getCanonicalName());
    }
  }

  @Test
  void testSelectMapAnnotation() throws Exception {
    SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      PersonMapper mapper = sqlSession.getMapper(PersonMapper.class);
      Map<Integer, Person> map = mapper.selectAsMap();
      assertEquals(map.size(), 2);
      assertEquals("java.util.LinkedHashMap", map.getClass().getCanonicalName());
    }
  }

  private SqlSessionFactory getSqlSessionFactoryXmlConfig(String resource) throws Exception {
    try (Reader configReader = Resources.getResourceAsReader(resource)) {
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);
      BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),"org/apache/ibatis/submitted/result_handler_type/CreateDB.sql");
      return sqlSessionFactory;
    }
  }

}
