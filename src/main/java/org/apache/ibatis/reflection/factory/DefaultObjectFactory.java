
package org.apache.ibatis.reflection.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.Reflector;

/**
 * 对象创建工厂，根据Class创建真实对象
 *  默认的对象工厂需要做的仅仅是实例化目标类， 要么通过默认构造方法，
 *  要么在参数映射存在的时候通过参数构造方法来实例化。
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable {

  private static final long serialVersionUID = -8855120656740914948L;

  @Override
  public <T> T create(Class<T> type) {
    return create(type, null, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    // 解析出Type的具体类型
    Class<?> classToCreate = resolveInterface(type);
    // we know types are assignable  // 实例化class
    return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
  }


  private <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    try {
      Constructor<T> constructor;
      // 通过无参构造函数创建对象
      if (constructorArgTypes == null || constructorArgs == null) {
        constructor = type.getDeclaredConstructor();
        try {
          return constructor.newInstance();
        } catch (IllegalAccessException e) {
          if (Reflector.canControlMemberAccessible()) {
            // 设置权限  使其可以访问 类中的私有方法  否则 报权限错误
            constructor.setAccessible(true);
            return constructor.newInstance();
          } else {
            throw e;
          }
        }
      }
      // 根据参数列表查找合适的构造函数，并实例化对象
      constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[constructorArgTypes.size()]));
      try {
        return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
      } catch (IllegalAccessException e) {
        if (Reflector.canControlMemberAccessible()) {
          constructor.setAccessible(true);
          return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
        } else {
          throw e;
        }
      }
    } catch (Exception e) {
      String argTypes = Optional.ofNullable(constructorArgTypes).orElseGet(Collections::emptyList).stream().map(Class::getSimpleName).collect(Collectors.joining(","));
      String argValues = Optional.ofNullable(constructorArgs).orElseGet(Collections::emptyList).stream().map(String::valueOf).collect(Collectors.joining(","));
      throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values (" + argValues + "). Cause: " + e, e);
    }
  }

  /**
   * 解析对象类型，模板方法子类可以重写
   */
  protected Class<?> resolveInterface(Class<?> type) {
    Class<?> classToCreate;
    if (type == List.class || type == Collection.class || type == Iterable.class) {
      classToCreate = ArrayList.class;
    } else if (type == Map.class) {
      classToCreate = HashMap.class;
    } else if (type == SortedSet.class) { // issue #510 Collections Support
      classToCreate = TreeSet.class;
    } else if (type == Set.class) {
      classToCreate = HashSet.class;
    } else {
      classToCreate = type;
    }
    return classToCreate;
  }

  @Override
  public <T> boolean isCollection(Class<T> type) {
    return Collection.class.isAssignableFrom(type);
  }
}
