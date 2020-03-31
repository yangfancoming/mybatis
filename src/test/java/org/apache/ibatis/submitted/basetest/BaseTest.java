
package org.apache.ibatis.submitted.basetest;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BaseTest extends MyBaseDataTest {

  public static final String XMLPATH = "org/apache/ibatis/submitted/basetest/mybatis-config.xml";
  public static final String DBSQL = "org/apache/ibatis/submitted/basetest/CreateDB.sql";

  @Test
  void shouldGetAUser() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = mapper.getUser(1);
    Assertions.assertEquals("User1", user.getName());
  }

  @Test
  void shouldInsertAUser() throws Exception {
    setUpByReader(XMLPATH,DBSQL);
    Mapper mapper = sqlSession.getMapper(Mapper.class);
    User user = new User();
    user.setId(2);
    user.setName("User2");
    mapper.insertUser(user);
  }

}
