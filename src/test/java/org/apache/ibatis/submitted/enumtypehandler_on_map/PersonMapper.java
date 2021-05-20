
package org.apache.ibatis.submitted.enumtypehandler_on_map;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonMapper {

    interface TypeName {
        Person.Type getType();
        String getName();
    }

    List<Person> getByType(@Param("type") Person.Type type, @Param("name") String name);
    List<Person> getByTypeNoParam(TypeName typeName);

}
