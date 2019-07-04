
package org.apache.ibatis.submitted.enumtypehandler_on_map;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PersonMapper {

    interface TypeName {
        Person.Type getType();
        String getName();
    }

    List<Person> getByType(@Param("type") Person.Type type, @Param("name") String name);
    List<Person> getByTypeNoParam(TypeName typeName);

}
