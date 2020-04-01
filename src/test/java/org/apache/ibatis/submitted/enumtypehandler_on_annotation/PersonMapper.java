
package org.apache.ibatis.submitted.enumtypehandler_on_annotation;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.submitted.enumtypehandler_on_annotation.Person.PersonType;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

/**
 * @since #444
 */
public interface PersonMapper {

    @ConstructorArgs({
            @Arg(column = "id", javaType = Integer.class, id = true)
            , @Arg(column = "firstName", javaType = String.class)
            , @Arg(column = "lastName", javaType = String.class)
            // target for test (ordinal number -> Enum constant)
            , @Arg(column = "personType", javaType = PersonType.class, typeHandler = EnumOrdinalTypeHandler.class)
    })
    @Select("SELECT id, firstName, lastName, personType FROM person WHERE id = #{id}")
    Person findOneUsingConstructor(int id);

    @Results({
            // target for test (ordinal number -> Enum constant)
            @Result(property = "personType", column = "personType", typeHandler = EnumOrdinalTypeHandler.class)
    })
    @Select("SELECT id, firstName, lastName, personType FROM person WHERE id = #{id}")
    Person findOneUsingSetter(int id);

    @TypeDiscriminator(
            // target for test (ordinal number -> Enum constant)
            column = "personType", javaType = PersonType.class, typeHandler = EnumOrdinalTypeHandler.class,
            // Switch using enum constant name(PERSON or EMPLOYEE) at cases attribute
            cases = {
                    @Case(value = "PERSON", type = Person.class, results = {@Result(property = "personType", column = "personType", typeHandler = EnumOrdinalTypeHandler.class)})
                    , @Case(value = "EMPLOYEE", type = Employee.class, results = {@Result(property = "personType", column = "personType", typeHandler = EnumOrdinalTypeHandler.class)})
            })
    @Select("SELECT id, firstName, lastName, personType FROM person WHERE id = #{id}")
    Person findOneUsingTypeDiscriminator(int id);

}
