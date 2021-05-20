

package org.apache.ibatis.datasource.unpooled;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.testcontainers.PgContainer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("TestcontainersTests")
public class NetworkTimeoutTest {

  @Test
  void testNetworkTimeout_UnpooledDataSource() throws Exception {
    UnpooledDataSource dataSource = (UnpooledDataSource) PgContainer.getUnpooledDataSource();
    dataSource.setDefaultNetworkTimeout(5000);
    try (Connection connection = dataSource.getConnection()) {
      assertEquals(5000, connection.getNetworkTimeout());
    }
  }

  @Test
  void testNetworkTimeout_PooledDataSource() throws Exception {
    UnpooledDataSource unpooledDataSource = (UnpooledDataSource) PgContainer.getUnpooledDataSource();
    PooledDataSource dataSource = new PooledDataSource(unpooledDataSource);
    dataSource.setDefaultNetworkTimeout(5000);
    try (Connection connection = dataSource.getConnection()) {
      assertEquals(5000, connection.getNetworkTimeout());
    }
  }

}
