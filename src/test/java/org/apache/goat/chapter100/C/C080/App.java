
package org.apache.goat.chapter100.C.C080;

import org.apache.common.MyBaseJavaMapperConfig;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class App extends MyBaseJavaMapperConfig {

  String resource = "org/apache/goat/chapter100/C/C080/AuthorMapper.xml";
  String statementId = "selectWithOptions";

  /**
   *  测试 局部xml配置 sql的所有属性
   * <select id="selectPerson" parameterType="int" parameterMap="deprecated" resultType="hashmap"
   * resultMap="personResultMap" flushCache="false" useCache="true" timeout="10"
   * fetchSize="256" statementType="PREPARED" resultSetType="FORWARD_ONLY">
  */
  @Test
  public void mappedStatementWithOptions() throws Exception {
    MappedStatement mappedStatement = getMappedStatement(resource,statementId);
    Assertions.assertEquals(Integer.valueOf(200), mappedStatement.getFetchSize());
    Assertions.assertEquals(Integer.valueOf(10), mappedStatement.getTimeout());
    Assertions.assertEquals(StatementType.PREPARED, mappedStatement.getStatementType());
    Assertions.assertEquals(ResultSetType.SCROLL_SENSITIVE, mappedStatement.getResultSetType());
    Assertions.assertFalse(mappedStatement.isFlushCacheRequired());
    Assertions.assertFalse(mappedStatement.isUseCache());
  }

}
