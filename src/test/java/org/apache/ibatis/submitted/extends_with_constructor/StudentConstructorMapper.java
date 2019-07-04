
package org.apache.ibatis.submitted.extends_with_constructor;

public interface StudentConstructorMapper {
  StudentConstructor selectWithTeacherById(Integer id);

  StudentConstructor selectNoNameById(Integer id);
}
