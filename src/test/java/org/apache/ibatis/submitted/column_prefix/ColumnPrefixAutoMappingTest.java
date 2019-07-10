
package org.apache.ibatis.submitted.column_prefix;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

class ColumnPrefixAutoMappingTest extends ColumnPrefixTest {

  @Override
  protected List<Pet> getPetAndRoom(SqlSession sqlSession) {
    List<Pet> pets = sqlSession.selectList("org.apache.ibatis.submitted.column_prefix.MapperAutoMapping.selectPets");
    return pets;
  }

  @Override
  protected List<Person> getPersons(SqlSession sqlSession) {
    List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.column_prefix.MapperAutoMapping.selectPersons");
    return list;
  }

  @Override
  protected String getConfigPath() {
    return "org/apache/ibatis/submitted/column_prefix/ConfigAutoMapping.xml";
  }

  @Test
  void testCaseInsensitivity() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Brand brand = sqlSession.selectOne("org.apache.ibatis.submitted.column_prefix.MapperAutoMapping.selectBrandWithProducts", 1);
      assertEquals(Integer.valueOf(1), brand.getId());
      assertEquals(2, brand.getProducts().size());
      assertEquals(Integer.valueOf(10), brand.getProducts().get(0).getId());
      assertEquals("alpha", brand.getProducts().get(0).getName());
    }
  }
}
