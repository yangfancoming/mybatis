
package org.apache.ibatis.submitted.parent_reference_3level;

public interface Mapper {

  Blog selectBlogByPrimaryKey(int aId);

  Blog selectBlogByPrimaryKeyColumnPrefix(int aId);
}
