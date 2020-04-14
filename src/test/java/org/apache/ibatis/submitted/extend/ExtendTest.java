
package org.apache.ibatis.submitted.extend;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtendTest extends MyBaseDataTest {

  @Test
  void testExtend() throws Exception {
    setUpByReader("org/apache/ibatis/submitted/extend/ExtendConfig.xml","org/apache/ibatis/submitted/extend/CreateDB.sql");
    ExtendMapper mapper = sqlSession.getMapper(ExtendMapper.class);
    Child answer = mapper.selectChild();
    assertEquals(answer.getMyProperty(), "last");
  }

}
