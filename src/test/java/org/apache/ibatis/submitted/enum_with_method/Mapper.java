
package org.apache.ibatis.submitted.enum_with_method;

import java.util.List;

public interface Mapper {

    User getUser(Integer id);

    List<User> getAll();

    void insertUser(User user);

}
