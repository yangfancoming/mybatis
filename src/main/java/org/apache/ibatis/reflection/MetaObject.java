
package org.apache.ibatis.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.CollectionWrapper;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * MetaObject用于获取和设置对象的属性值
 *
 * MetaObject 内部具有5个字段,对外提供的所有方法都是基于这5个字段的。
 * 将这5个字段的方法取一部分对外部提供服务的组合模式
 *
 * MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值。其中originalObject最主要的有三种类型:
 * 1.Map类型
 * 2.Collection类型
 * 3.普通的java对象，有get和set方法的对象
 * getValue和setValue中的name参数支持复杂的属性访问：例如user.cust.custId  或 user.acts[0].acctId！
*/
public class MetaObject {

  /**原始对象*/  //原始的对象
  private final Object originalObject;
  /**对象包装器*/ //对原始对象的一个包装 // ObjectWrapper 是一个对象包装器,提供了对Bean,Collection,Map 不同的操作方式
  private final ObjectWrapper objectWrapper;
  /**对象工厂*/
  private final ObjectFactory objectFactory;
  /**对象包装器工厂*/
  private final ObjectWrapperFactory objectWrapperFactory;
  /**反射器工厂*/
  private final ReflectorFactory reflectorFactory;

  // 私有的元对象构造方法 MetaObject 的构造器是私有的,只提供 forObject 创建MetaObject对象.
  private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    this.originalObject = object;
    this.objectFactory = objectFactory;
    this.objectWrapperFactory = objectWrapperFactory;
    this.reflectorFactory = reflectorFactory;

    //通过原始对象类型获取对象包装器 // 根据传入object类型不同，指定不同的wrapper
    if (object instanceof ObjectWrapper) {
      this.objectWrapper = (ObjectWrapper) object;
    } else if (objectWrapperFactory.hasWrapperFor(object)) {
      this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
    } else if (object instanceof Map) {
      this.objectWrapper = new MapWrapper(this, (Map) object);
    } else if (object instanceof Collection) {
      this.objectWrapper = new CollectionWrapper(this, (Collection) object);
    } else {
      this.objectWrapper = new BeanWrapper(this, object);
    }
  }

  // 获取对象的元对象
  public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    // 当参数object为null时,返回 SystemMetaObject.NullObject.class 对象的元数据
    if (object == null) {
      return SystemMetaObject.NULL_META_OBJECT;
    } else {
      return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }
  }

  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }

  public ObjectWrapperFactory getObjectWrapperFactory() {
    return objectWrapperFactory;
  }

  public ReflectorFactory getReflectorFactory() {
    return reflectorFactory;
  }

  public Object getOriginalObject() {
    return originalObject;
  }

  public String findProperty(String propName, boolean useCamelCaseMapping) {
    return objectWrapper.findProperty(propName, useCamelCaseMapping);
  }

  public String[] getGetterNames() {
    return objectWrapper.getGetterNames();
  }

  public String[] getSetterNames() {
    return objectWrapper.getSetterNames();
  }

  public Class<?> getSetterType(String name) {
    return objectWrapper.getSetterType(name);
  }

  public Class<?> getGetterType(String name) {
    return objectWrapper.getGetterType(name);
  }

  public boolean hasSetter(String name) {
    return objectWrapper.hasSetter(name);
  }

  public boolean hasGetter(String name) {
    return objectWrapper.hasGetter(name);
  }

  //从originalObject获取属性值
  public Object getValue(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        return null;
      } else {
        //这里相当于递归调用，直到最后一层。例如user.cust.custId
        //第一次递归cust.custId
        //第二次递归custId，这个就是真正访问要返回的
        return metaValue.getValue(prop.getChildren());
      }
    } else {
      return objectWrapper.get(prop);
    }
  }

  //设置originalObject属性值
  public void setValue(String name, Object value) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        if (value == null) {
          // don't instantiate child path if value is null
          return;
        } else {
          metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
        }
      }
      metaValue.setValue(prop.getChildren(), value);
    } else {
      objectWrapper.set(prop, value);
    }
  }

  // 获取属性的元对象，因为对象属性也是对象
  public MetaObject metaObjectForProperty(String name) {
    Object value = getValue(name);
    return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
  }

  public ObjectWrapper getObjectWrapper() {
    return objectWrapper;
  }

  public boolean isCollection() {
    return objectWrapper.isCollection();
  }

  //应该是对collection的操作
  public void add(Object element) {
    objectWrapper.add(element);
  }

  //应该是对collection的操作
  public <E> void addAll(List<E> list) {
    objectWrapper.addAll(list);
  }
}
