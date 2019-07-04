
package org.apache.ibatis.reflection.wrapper;

import org.apache.ibatis.reflection.MetaObject;

/**
 * @author Clinton Begin
 */
public interface ObjectWrapperFactory {

  boolean hasWrapperFor(Object object);

  ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
