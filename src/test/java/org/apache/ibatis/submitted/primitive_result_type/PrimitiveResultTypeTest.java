
package org.apache.ibatis.submitted.primitive_result_type;

import org.apache.ibatis.BaseDataTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class PrimitiveResultTypeTest {

  @BeforeAll
  static void setup() throws Exception {
    BaseDataTest.runScript(IbatisConfig.getSqlSessionFactory().getConfiguration().getEnvironment().getDataSource(),
            "org/apache/ibatis/submitted/primitive_result_type/create.sql");
  }

  @Test
  void shouldReturnProperPrimitiveType() {
    List<Integer> codes = ProductDAO.selectProductCodes();
    for (Object code : codes) {
      assertTrue(code instanceof Integer);
    }
    List<Long> lcodes = ProductDAO.selectProductCodesL();
    for (Object lcode : lcodes) {
      assertTrue(!(lcode instanceof Integer));
    }
    List<BigDecimal> bcodes = ProductDAO.selectProductCodesB();
    for (Object bcode : bcodes) {
      assertTrue(bcode instanceof BigDecimal);
    }
  }
  @Test
  void noErrorThrowOut(){
      List<Product> products=ProductDAO.selectAllProducts();
    assertEquals(4, products.size(), "should return 4 results");
  }
}
