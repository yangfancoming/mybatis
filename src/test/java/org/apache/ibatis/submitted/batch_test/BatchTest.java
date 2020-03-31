
package org.apache.ibatis.submitted.batch_test;

import org.apache.common.MyBaseDataTest;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BatchTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/batch_test/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/batch_test/CreateDB.sql";

  // 测试 ExecutorType.BATCH
  @Test
  void shouldGetAUserNoException() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false)) {
      try {
        Mapper mapper = sqlSession.getMapper(Mapper.class);
        User user = mapper.getUser(1);
        user.setId(2);
        user.setName("User2");
        mapper.insertUser(user);
        Assertions.assertEquals("Dept1", mapper.getUser(2).getDept().getName());
      } finally {
        sqlSession.commit();
      }
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

}
