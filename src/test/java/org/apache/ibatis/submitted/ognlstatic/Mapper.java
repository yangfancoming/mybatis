
package org.apache.ibatis.submitted.ognlstatic;

public interface Mapper {

  User getUserStatic(Integer id);

  User getUserIfNode(String id);

}
