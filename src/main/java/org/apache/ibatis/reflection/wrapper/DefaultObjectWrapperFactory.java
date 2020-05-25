
package org.apache.ibatis.reflection.wrapper;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;


public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

  private static final Log log = LogFactory.getLog(DefaultObjectWrapperFactory.class);

  public DefaultObjectWrapperFactory() { // -modify
    log.warn("进入 【DefaultObjectWrapperFactory】 无参 构造函数 {}");
  }

  @Override
  public boolean hasWrapperFor(Object object) {
    return false;
  }

  @Override
  public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
    throw new ReflectionException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
  }
}
