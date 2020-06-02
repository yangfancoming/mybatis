
package org.apache.ibatis.reflection.property;

import java.lang.reflect.Field;
import org.apache.ibatis.reflection.Reflector;

// 顾名思义，这个类就是就是将一个对象的属性值赋给另一个对象中对应的属性。
public final class PropertyCopier {

  private PropertyCopier() {
    // Prevent Instantiation of Static Class
  }

  public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
    Class<?> parent = type;
    while (parent != null) {
      final Field[] fields = parent.getDeclaredFields();
      for (Field field : fields) {
        try {
          try {
            field.set(destinationBean, field.get(sourceBean));
          } catch (IllegalAccessException e) {
            if (Reflector.canControlMemberAccessible()) {
              //因为getDeclaredFields函数返回的这个类中各种限定符的属性，如果不设置accessible为true,在调用限定符是private的属性时会报错
              field.setAccessible(true);
              field.set(destinationBean, field.get(sourceBean));
            } else {
              throw e;
            }
          }
        } catch (Exception e) {
          // Nothing useful to do, will only fail on final fields, which will be ignored.
        }
      }
      // 本类执行完成后，查看父类
      parent = parent.getSuperclass();
    }
  }
}
