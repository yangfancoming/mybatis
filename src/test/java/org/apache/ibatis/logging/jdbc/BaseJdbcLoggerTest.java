
package org.apache.ibatis.logging.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Array;

import org.apache.ibatis.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaseJdbcLoggerTest {

  @Mock
  Log log;
  @Mock
  Array array;
  private BaseJdbcLogger logger;

  @BeforeEach
  void setUp() {
    logger = new BaseJdbcLogger(log, 1) {
    };
  }

  @Test
  void shouldDescribePrimitiveArrayParameter() throws Exception {
    logger.setColumn("1", array);
    when(array.getArray()).thenReturn(new int[] { 1, 2, 3 });
    assertThat(logger.getParameterValueString()).startsWith("[1, 2, 3]");
  }

  @Test
  void shouldDescribeObjectArrayParameter() throws Exception {
    logger.setColumn("1", array);
    when(array.getArray()).thenReturn(new String[] { "one", "two", "three" });
    assertThat(logger.getParameterValueString()).startsWith("[one, two, three]");
  }
}
