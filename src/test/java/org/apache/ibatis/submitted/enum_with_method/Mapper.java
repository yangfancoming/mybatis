
package org.apache.ibatis.submitted.enum_with_method;

public interface Mapper {

    User getUser(Integer id);

    void insertUser(User user);

}
