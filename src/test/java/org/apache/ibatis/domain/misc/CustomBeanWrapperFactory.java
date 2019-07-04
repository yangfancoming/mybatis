
package org.apache.ibatis.domain.misc;

import org.apache.ibatis.domain.blog.Author;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

public class CustomBeanWrapperFactory implements ObjectWrapperFactory {
  @Override
  public boolean hasWrapperFor(Object object) {
    if (object instanceof Author) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
    return new CustomBeanWrapper(metaObject, object);
  }
}
