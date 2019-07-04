
package org.apache.ibatis.submitted.xml_references;

import java.util.List;

public interface PersonMapper2 {

    interface PersonType {
        Person.Type getType();
    }

    List<Person> selectAllByType(Person.Type type);
    List<Person> selectAllByTypeNameAttribute(Person.Type type);
    List<Person> selectAllByTypeWithInterface(PersonType personType);
    List<Person> selectAllByTypeNameAttributeWithInterface(PersonType personType);
}
