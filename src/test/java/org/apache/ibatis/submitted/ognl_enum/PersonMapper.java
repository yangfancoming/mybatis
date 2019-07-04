
package org.apache.ibatis.submitted.ognl_enum;

import java.util.List;

public interface PersonMapper {

    interface PersonType {
        Person.Type getType();
    }

    List<Person> selectAllByType(Person.Type type);
    List<Person> selectAllByTypeNameAttribute(Person.Type type);
    List<Person> selectAllByTypeWithInterface(PersonType personType);
    List<Person> selectAllByTypeNameAttributeWithInterface(PersonType personType);
}
