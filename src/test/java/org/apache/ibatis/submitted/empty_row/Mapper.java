
package org.apache.ibatis.submitted.empty_row;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface Mapper {

  @Select("select null from (values(0))")
  String getString();

  @ResultMap("parentRM")
  @Select("select col1, col2 from parent where id = #{id}")
  Parent getBean(Integer id);

  @Select("select col1, col2 from parent where id = #{id}")
  Map<String, String> getMap(Integer id);

  @ResultMap("associationRM")
  @Select({ "select p.id, c.name child_name from parent p left join child c on c.parent_id = p.id where p.id = #{id}" })

  Parent getAssociation(Integer id);

  @ResultMap("associationWithNotNullColumnRM")
  @Select({ "select p.id, c.id child_id, c.name child_name from parent p",
      "left join child c on c.parent_id = p.id where p.id = #{id}" })
  Parent getAssociationWithNotNullColumn(Integer id);

  @ResultMap("nestedAssociationRM")
  @Select("select 1 id, null child_name, null grandchild_name from (values(0))")
  Parent getNestedAssociation();

  @ResultMap("collectionRM")
  @Select({ "select p.id, c.name child_name from parent p left join child c on c.parent_id = p.id where p.id = #{id}" })
  Parent getCollection(Integer id);

  @ResultMap("twoCollectionsRM")
  @Select({ "select p.id, c.name child_name, e.name pet_name from parent p",
      "left join child c on c.parent_id = p.id",
      "left join pet e on e.parent_id = p.id", "where p.id = #{id}" })
  Parent getTwoCollections(Integer id);
}
