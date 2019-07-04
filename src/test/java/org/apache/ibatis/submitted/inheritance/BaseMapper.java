
package org.apache.ibatis.submitted.inheritance;

public interface BaseMapper<T> {

  T retrieveById(Integer id);

}
