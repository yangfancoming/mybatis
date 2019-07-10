
package org.apache.ibatis.submitted.column_prefix;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

class ColumnPrefixNestedQueryTest extends ColumnPrefixTest {

  @Override
  protected List<Pet> getPetAndRoom(SqlSession sqlSession) {
    List<Pet> pets = sqlSession.selectList("org.apache.ibatis.submitted.column_prefix.MapperNestedQuery.selectPets");
    return pets;
  }

  @Override
  protected List<Person> getPersons(SqlSession sqlSession) {
    List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.column_prefix.MapperNestedQuery.selectPersons");
    return list;
  }

  @Override
  protected String getConfigPath() {
    return "org/apache/ibatis/submitted/column_prefix/ConfigNestedQuery.xml";
  }
}
