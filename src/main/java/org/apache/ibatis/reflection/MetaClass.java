
package org.apache.ibatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * MetaClass则用于获取类相关的信息
 * 
 * 白话理解：
 * 除了使用type()动态创建类以外，要控制类的创建行为，还可以使用metaclass，直译为元类，简单的解释就是：
 * 当我们定义了类以后，就可以根据这个类创建出实例，所以：先定义类，然后创建实例。
 * 但是如果我们想动态创建出类呢？那就必须根据metaclass动态创建出类，所以：先定义metaclass，然后创建类。
 * 连接起来就是：先定义metaclass，就可以创建类，最后创建实例。
 *
 *  元信息类 MetaClass 的构造方法为私有类型，所以不能直接创建，必须使用其提供的 forClass 方法进行创建
 *  1. ReflectorFactory： 顾名思义， Reflector 的工厂类，兼有缓存 Reflector 对象的功能
 *  2. Reflector： 反射器，用于解析和存储目标类中的元信息
 *  3. PropertyTokenizer： 属性名分词器，用于处理较为复杂的属性名
*/
public class MetaClass {

  private final ReflectorFactory reflectorFactory;
  private final Reflector reflector;

  // 私有化构造函数
  private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
    this.reflectorFactory = reflectorFactory;
    // 根据类型创建 Reflector
    this.reflector = reflectorFactory.findForClass(type);
  }

  // 调用构造方法创建MetaClass
  public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
    return new MetaClass(type, reflectorFactory);
  }

  // 通过属性名称， 获取属性的MetaClass(解决成员变量是类的情况)
  public MetaClass metaClassForProperty(String name) {
    Class<?> propType = reflector.getGetterType(name);
    return MetaClass.forClass(propType, reflectorFactory);
  }

  public String findProperty(String name) {
    StringBuilder prop = buildProperty(name, new StringBuilder());
    return prop.length() > 0 ? prop.toString() : null;
  }

  public String findProperty(String name, boolean useCamelCaseMapping) {
    if (useCamelCaseMapping) {
      name = name.replace("_", "");
    }
    return findProperty(name);
  }

  public String[] getGetterNames() {
    return reflector.getGetablePropertyNames();
  }

  public String[] getSetterNames() {
    return reflector.getSetablePropertyNames();
  }

  public Class<?> getSetterType(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaClass metaProp = metaClassForProperty(prop.getName());
      return metaProp.getSetterType(prop.getChildren());
    } else {
      return reflector.getSetterType(prop.getName());
    }
  }

  public Class<?> getGetterType(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaClass metaProp = metaClassForProperty(prop);
      return metaProp.getGetterType(prop.getChildren());
    }
    // issue #506. Resolve the type inside a Collection Object
    return getGetterType(prop);
  }

  private MetaClass metaClassForProperty(PropertyTokenizer prop) {
    Class<?> propType = getGetterType(prop);
    return MetaClass.forClass(propType, reflectorFactory);
  }

  private Class<?> getGetterType(PropertyTokenizer prop) {
    Class<?> type = reflector.getGetterType(prop.getName());
    if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
      Type returnType = getGenericGetterType(prop.getName());
      if (returnType instanceof ParameterizedType) {
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length == 1) {
          returnType = actualTypeArguments[0];
          if (returnType instanceof Class) {
            type = (Class<?>) returnType;
          } else if (returnType instanceof ParameterizedType) {
            type = (Class<?>) ((ParameterizedType) returnType).getRawType();
          }
        }
      }
    }
    return type;
  }

  private Type getGenericGetterType(String propertyName) {
    try {
      Invoker invoker = reflector.getGetInvoker(propertyName);
      if (invoker instanceof MethodInvoker) {
        Field _method = MethodInvoker.class.getDeclaredField("method");
        _method.setAccessible(true);
        Method method = (Method) _method.get(invoker);
        return TypeParameterResolver.resolveReturnType(method, reflector.getType());
      } else if (invoker instanceof GetFieldInvoker) {
        Field _field = GetFieldInvoker.class.getDeclaredField("field");
        _field.setAccessible(true);
        Field field = (Field) _field.get(invoker);
        return TypeParameterResolver.resolveFieldType(field, reflector.getType());
      }
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
    }
    return null;
  }


  public boolean hasSetter(String name) {
    // 属性分词器，用于解析属性名
    PropertyTokenizer prop = new PropertyTokenizer(name);
    // hasNext 返回 true，则表明 name 是一个复合属性，后面会进行分析
    if (prop.hasNext()) {
      // 调用 reflector 的 hasSetter 方法
      if (reflector.hasSetter(prop.getName())) {
        // 为属性创建创建 MetaClass
        MetaClass metaProp = metaClassForProperty(prop.getName());
        // 递归调用
        return metaProp.hasSetter(prop.getChildren());
      } else {
        return false;
      }
    } else {
      // 调用 reflector 的 hasSetter 方法
      return reflector.hasSetter(prop.getName());
    }
  }

  public boolean hasGetter(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      if (reflector.hasGetter(prop.getName())) {
        MetaClass metaProp = metaClassForProperty(prop);
        return metaProp.hasGetter(prop.getChildren());
      } else {
        return false;
      }
    } else {
      return reflector.hasGetter(prop.getName());
    }
  }

  public Invoker getGetInvoker(String name) {
    return reflector.getGetInvoker(name);
  }

  public Invoker getSetInvoker(String name) {
    return reflector.getSetInvoker(name);
  }

  /**
   * 验证传入的表达式，是否存在指定的字段
   * 解析属性表达式 会去寻找reflector中是否有对应的的属性
   */
  private StringBuilder buildProperty(String name, StringBuilder builder) {
    // 映射文件表达式迭代器  // 解析属性表达式
    PropertyTokenizer prop = new PropertyTokenizer(name);
    // 是否有子表达式
    if (prop.hasNext()) {
      // 查找对应的属性 // 复杂表达式，如name = items[0].name，则prop.getName() = items
      String propertyName = reflector.findPropertyName(prop.getName());
      if (propertyName != null) {
        // 追加属性名
        builder.append(propertyName);
        // items.
        builder.append(".");
        // 创建对应的 MetaClass 对象  // 加载内嵌字段类型对应的MetaClass
        MetaClass metaProp = metaClassForProperty(propertyName);
        // 解析子表达式, 递归 // 迭代子字段
        metaProp.buildProperty(prop.getChildren(), builder);
      }
    } else {
      // 根据名称查找属性 // 非复杂表达式，获取字段名，如：userid->userId
      String propertyName = reflector.findPropertyName(name);
      if (propertyName != null) {
        builder.append(propertyName);
      }
    }
    return builder;
  }

  public boolean hasDefaultConstructor() {
    return reflector.hasDefaultConstructor();
  }
}
