
package org.apache.ibatis.submitted.serializecircular;

import java.io.Reader;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

//@Disabled("see issue #614")
class SerializeCircularTest {

  @Test
  void serializeAndDeserializeObjectsWithAggressiveLazyLoadingWithoutPreloadingAttribute()
  throws Exception {
    try (SqlSession sqlSession = createSessionWithAggressiveLazyLoading()) {
      testSerializeWithoutPreloadingAttribute(sqlSession);
    }
  }

  @Test
  void serializeAndDeserializeObjectsWithAggressiveLazyLoadingWithPreloadingAttribute()
  throws Exception {
    try (SqlSession sqlSession = createSessionWithAggressiveLazyLoading()) {
      testSerializeWithPreloadingAttribute(sqlSession);
    }
  }

//  @Disabled("See http://code.google.com/p/mybatis/issues/detail?id=614")
  @Test
  void serializeAndDeserializeObjectsWithoutAggressiveLazyLoadingWithoutPreloadingAttribute()
  throws Exception {
    try (SqlSession sqlSession = createSessionWithoutAggressiveLazyLoading()) {
        //expected problem with deserializing
      testSerializeWithoutPreloadingAttribute(sqlSession);
    }
  }

  @Test
  void serializeAndDeserializeObjectsWithoutAggressiveLazyLoadingWithPreloadingAttribute()
  throws Exception {
    try (SqlSession sqlSession = createSessionWithoutAggressiveLazyLoading()) {
      testSerializeWithPreloadingAttribute(sqlSession);
    }
  }

  private SqlSession createSessionWithoutAggressiveLazyLoading() throws Exception {
    return createSession(false);
  }

  private SqlSession createSessionWithAggressiveLazyLoading() throws Exception {
    return createSession(true);
  }

  private SqlSession createSession(boolean anAggressiveLazyLoading) throws Exception {
    String xmlConfig = anAggressiveLazyLoading ?
        "org/apache/ibatis/submitted/serializecircular/MapperConfigWithAggressiveLazyLoading.xml":
        "org/apache/ibatis/submitted/serializecircular/MapperConfigWithoutAggressiveLazyLoading.xml";
      SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
    return sqlSessionFactory.openSession();
  }

  private void testSerializeWithPreloadingAttribute(SqlSession sqlSession) {
    testSerialize(sqlSession, true);
  }

  private void testSerializeWithoutPreloadingAttribute(SqlSession sqlSession) {
    testSerialize(sqlSession, false);
  }

  private void testSerialize(SqlSession sqlSession, boolean aPreloadAttribute) {
    DepartmentMapper departmentMapper = sqlSession.getMapper(DepartmentMapper.class);
    Department department = departmentMapper.getById(1);
    if (aPreloadAttribute) {
      department.getAttribute();
    }

    serializeAndDeserializeObject(department);

    // This call results in problems when deserializing department
    department.getPerson();
    serializeAndDeserializeObject(department);
  }

  void serializeAndDeserializeObject(Object anObject) {
    UtilityTester.serializeAndDeserializeObject(anObject);
  }

  private SqlSessionFactory getSqlSessionFactoryXmlConfig(String resource) throws Exception {
    try (Reader configReader = Resources.getResourceAsReader(resource)) {
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configReader);

      BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
              "org/apache/ibatis/submitted/serializecircular/CreateDB.sql");

      return sqlSessionFactory;
    }
  }

}
