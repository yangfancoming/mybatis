
package org.apache.ibatis.submitted.custom_collection_handling;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

import java.util.List;

public class CustomObjectWrapper implements org.apache.ibatis.reflection.wrapper.ObjectWrapper {

  private CustomCollection collection;

  public CustomObjectWrapper(CustomCollection collection) {
    this.collection = collection;
  }

  @Override
  public Object get(PropertyTokenizer prop) {
    // Not Implemented
    return null;
  }

  @Override
  public void set(PropertyTokenizer prop, Object value) {
    // Not Implemented
  }

  @Override
  public String findProperty(String name, boolean useCamelCaseMapping) {
    // Not Implemented
    return null;
  }

  @Override
  public String[] getGetterNames() {
    // Not Implemented
    return null;
  }

  @Override
  public String[] getSetterNames() {
    // Not Implemented
    return null;
  }

  @Override
  public Class<?> getSetterType(String name) {
    // Not Implemented
    return null;
  }

  @Override
  public Class<?> getGetterType(String name) {
    // Not Implemented
    return null;
  }

  @Override
  public boolean hasSetter(String name) {
    // Not Implemented
    return false;
  }

  @Override
  public boolean hasGetter(String name) {
    // Not Implemented
    return false;
  }

  @Override
  public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
    // Not Implemented
    return null;
  }

  @Override
  public boolean isCollection() {
    return true;
  }

  @Override
  public void add(Object element) {
    ((CustomCollection<Object>) collection).add(element);
  }

  @Override
  public <E> void addAll(List<E> element) {
    ((CustomCollection<Object>) collection).addAll(element);
  }

}
