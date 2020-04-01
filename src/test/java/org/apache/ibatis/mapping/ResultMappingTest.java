
package org.apache.ibatis.mapping;

import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultMappingTest {
  @Mock
  private Configuration configuration;

  // Issue 697: Association with both a resultMap and a select attribute should throw exception
  @Test
  void shouldThrowErrorWhenBothResultMapAndNestedSelectAreSet() {
    Assertions.assertThrows(IllegalStateException.class, () -> new ResultMapping.Builder(configuration, "prop")
      .nestedQueryId("nested query ID")
      .nestedResultMapId("nested resultMap")
      .build());
  }

  //Issue 4: column is mandatory on nested queries
  @Test
  void shouldFailWithAMissingColumnInNetstedSelect() {
    Assertions.assertThrows(IllegalStateException.class, () -> new ResultMapping.Builder(configuration, "prop")
        .nestedQueryId("nested query ID")
        .build());
  }

}
