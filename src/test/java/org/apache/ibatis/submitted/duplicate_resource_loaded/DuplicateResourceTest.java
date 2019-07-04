
package org.apache.ibatis.submitted.duplicate_resource_loaded;

import org.junit.jupiter.api.Assertions;
import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;
import java.util.Map;

class DuplicateResourceTest extends BaseDataTest {

  @BeforeEach
  void setup() throws Exception {
    BaseDataTest.createBlogDataSource();
  }

  @Test
  void shouldDemonstrateDuplicateResourceIssue() throws Exception {
    final String resource = "org/apache/ibatis/submitted/duplicate_resource_loaded/Config.xml";
    final Reader reader = Resources.getResourceAsReader(resource);
    final SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    final SqlSessionFactory factory = builder.build(reader);
    try (SqlSession sqlSession = factory.openSession()) {
      final Mapper mapper = sqlSession.getMapper(Mapper.class);
      final List<Map<String, Object>> list = mapper.selectAllBlogs();
      Assertions.assertEquals(2,list.size());
    }
  }
}
