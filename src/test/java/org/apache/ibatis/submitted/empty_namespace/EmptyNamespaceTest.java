
package org.apache.ibatis.submitted.empty_namespace;

import java.io.Reader;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// 源码搜索串    if (namespace == null || namespace.equals(""))
public class EmptyNamespaceTest {

  // 测试源码中  namespace.equals("") 的情况
  @Test
  void testEmptyNamespaceWithEmpty() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/empty_namespace/ibatisConfig.xml")) {
        Assertions.assertThrows(PersistenceException.class, () -> new SqlSessionFactoryBuilder().build(reader));
    }
  }

  // 测试源码中  namespace == null  的情况
  @Test
  void testEmptyNamespaceWithNull() throws Exception {
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/empty_namespace/ibatisConfig2.xml")) {
      new SqlSessionFactoryBuilder().build(reader);
    }
  }
}
