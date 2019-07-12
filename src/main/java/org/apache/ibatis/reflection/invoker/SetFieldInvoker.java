
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.Field;

import org.apache.ibatis.reflection.Reflector;


// 如果没有getter， 则使用该方法， 通过Field类直接读取成员变量的值
public class SetFieldInvoker implements Invoker {
  private final Field field;

  public SetFieldInvoker(Field field) {
    this.field = field;
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException {
    try {
      field.set(target, args[0]);
    } catch (IllegalAccessException e) {
      if (Reflector.canControlMemberAccessible()) {
        field.setAccessible(true);
        field.set(target, args[0]);
      } else {
        throw e;
      }
    }
    return null;
  }

  @Override
  public Class<?> getType() {
    return field.getType();
  }
}
