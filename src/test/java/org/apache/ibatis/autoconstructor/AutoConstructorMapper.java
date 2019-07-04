
package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AutoConstructorMapper {
  @Select("SELECT * FROM subject WHERE id = #{id}")
  PrimitiveSubject getSubject(final int id);

  @Select("SELECT * FROM subject")
  List<PrimitiveSubject> getSubjects();

  @Select("SELECT * FROM subject")
  List<AnnotatedSubject> getAnnotatedSubjects();

  @Select("SELECT * FROM subject")
  List<BadSubject> getBadSubjects();

  @Select("SELECT * FROM extensive_subject")
  List<ExtensiveSubject> getExtensiveSubject();
}
