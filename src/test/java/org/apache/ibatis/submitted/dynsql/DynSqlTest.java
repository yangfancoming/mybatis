
package org.apache.ibatis.submitted.dynsql;

import org.apache.common.MyBaseDataTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynSqlTest extends MyBaseDataTest {

  @BeforeAll
  static void setUp() throws Exception {
    setUpByReader("org/apache/ibatis/submitted/dynsql/MapperConfig.xml","org/apache/ibatis/submitted/dynsql/CreateDB.sql");
  }

  @Test
  void testSelect() {
    List<Integer> ids = Arrays.asList(1,3,5);
    Parameter parameter = new Parameter("ibtest",ids,true);
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select", parameter);
    assertEquals(3, answer.size());
  }

  @Test
  void testSelectSimple() {
    List<Integer> ids = Arrays.asList(1,3,5);
    Parameter parameter = new Parameter("ibtest",ids,true);
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select_simple", parameter);
    assertEquals(3, answer.size());
  }

  @Test  // 测试 bind标签
  void testSelectLike() {
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectLike", "Ba");
    assertEquals(2, answer.size());
    assertEquals(4, answer.get(0).get("id"));
    assertEquals(6, answer.get(1).get("id"));
  }

  @Test
  void testNumerics() {
    List<NumericRow> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectNumerics");
    assertEquals(1, answer.size());
    NumericRow row = answer.get(0);
    assertEquals(1, (int) row.getId());
    assertEquals(2, (int) row.getTinynumber());
    assertEquals(3, (int) row.getSmallnumber());
    assertEquals(4L, (long) row.getLonginteger());
    assertEquals(new BigInteger("5"), row.getBiginteger());
    assertEquals(new BigDecimal("6.00"), row.getNumericnumber());
    assertEquals(new BigDecimal("7.00"), row.getDecimalnumber());
    assertEquals((Float) 8.0f, row.getRealnumber());
    assertEquals((Float) 9.0f, row.getFloatnumber());
    assertEquals((Double) 10.0, row.getDoublenumber());
  }

  @Test
  void testOgnlStaticMethodCall() {
    List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.ognlStaticMethodCall", "Rock 'n Roll");
    assertEquals(1, answer.size());
    assertEquals(7, answer.get(0).get("ID"));
  }

}
