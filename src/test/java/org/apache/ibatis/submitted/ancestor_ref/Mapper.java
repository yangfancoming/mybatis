
package org.apache.ibatis.submitted.ancestor_ref;

public interface Mapper {

  User getUserAssociation(Integer id);

  User getUserCollection(Integer id);

  Blog selectBlog(Integer id);

}
