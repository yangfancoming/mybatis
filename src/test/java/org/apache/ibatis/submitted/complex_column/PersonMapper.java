
package org.apache.ibatis.submitted.complex_column;

import org.apache.ibatis.annotations.*;

public interface PersonMapper {

    Person getWithoutComplex(Long id);
    Person getWithComplex(Long id);
    Person getParentWithComplex(Person person);

    @Select({
      "SELECT id, firstName, lastName, parent_id, parent_firstName, parent_lastName",
      "FROM Person",
      "WHERE id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("personMapComplex")
    Person getWithComplex2(Long id);

    @Select({
        "SELECT id, firstName, lastName, parent_id, parent_firstName, parent_lastName",
        "FROM Person",
        "WHERE id = #{id,jdbcType=INTEGER}"
      })
    @ResultMap("org.apache.ibatis.submitted.complex_column.PersonMapper.personMapComplex")
    Person getWithComplex3(Long id);


    @Select({
            "SELECT id, firstName, lastName, parent_id, parent_firstName, parent_lastName",
            "FROM Person",
            "WHERE id = #{id,jdbcType=INTEGER}"
    })
    @Results({
            @Result(id=true, column = "id", property = "id"),
            @Result(property = "parent", column="{firstName=parent_firstName,lastName=parent_lastName}", one=@One(select="getParentWithParamAttributes"))

    })
    Person getComplexWithParamAttributes(Long id);

    @Select("SELECT id, firstName, lastName, parent_id, parent_firstName, parent_lastName" +
            " FROM Person" +
            " WHERE firstName = #{firstName,jdbcType=VARCHAR}" +
            " AND lastName = #{lastName,jdbcType=VARCHAR}" +
            " LIMIT 1")
    Person getParentWithParamAttributes(@Param("firstName") String firstName, @Param("lastName") String lastname);
}
