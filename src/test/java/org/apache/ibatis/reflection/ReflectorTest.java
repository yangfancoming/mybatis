
package org.apache.ibatis.reflection;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.model.BeanInterface;
import org.apache.ibatis.reflection.model.Child;
import org.apache.ibatis.reflection.model.Section;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.submitted.extends_with_constructor.Student;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReflectorTest {

  ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

  @Test
  void fuck() {
    Class<Configuration> aClass = Configuration.class;
    Reflector reflector = new Reflector(aClass);
    Method[] classMethods = reflector.getClassMethods(aClass);
    System.out.println(classMethods.length);
  }

  @Test
  void testGetablePropertyNames() {
    Reflector reflector = reflectorFactory.findForClass(Configuration.class);
    Assert.assertEquals(false,reflector.hasSetter("NoExist"));
    Assert.assertEquals(true,reflector.hasSetter("mapUnderscoreToCamelCase"));
    Assert.assertEquals(true,reflector.hasSetter("multipleResultSetsEnabled"));
  }

  @Test
  void testGetGetterType() {
    Reflector section_reflector = reflectorFactory.findForClass(Section.class);
    Assertions.assertEquals(Long.class, section_reflector.getGetterType("id"));
  }

  @Test
  void testGetSetterType() {
    Reflector section_reflector = reflectorFactory.findForClass(Section.class);
    Assertions.assertEquals(Long.class, section_reflector.getSetterType("id"));
  }

  @Test
  void shouldNotGetClass() {
    Reflector section_reflector = reflectorFactory.findForClass(Section.class);
    Assertions.assertFalse(section_reflector.hasGetter("class"));
  }

  @Test
  void shouldResolveSetterParam() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(String.class, reflector.getSetterType("id"));
  }

  @Test
  void shouldResolveParameterizedSetterParam() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(List.class, reflector.getSetterType("list"));
  }

  @Test
  void shouldResolveArraySetterParam() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    Class<?> clazz = reflector.getSetterType("array");
    assertTrue(clazz.isArray());
    assertEquals(String.class, clazz.getComponentType());
  }

  @Test
  void shouldResolveGetterType() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(String.class, reflector.getGetterType("id"));
  }

  @Test
  void shouldResolveSetterTypeFromPrivateField() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(String.class, reflector.getSetterType("fld"));
  }

  @Test
  void shouldResolveGetterTypeFromPublicField() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(String.class, reflector.getGetterType("pubFld"));
  }

  @Test
  void shouldResolveParameterizedGetterType() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    assertEquals(List.class, reflector.getGetterType("list"));
  }

  @Test
  void shouldResolveArrayGetterType() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    Class<?> clazz = reflector.getGetterType("array");
    assertTrue(clazz.isArray());
    assertEquals(String.class, clazz.getComponentType());
  }

  @Test
  void shouldResoleveReadonlySetterWithOverload() {
    class BeanClass implements BeanInterface<String> {
      @Override
      public void setId(String id) {
        // Do nothing
      }
    }
    Reflector reflector = reflectorFactory.findForClass(BeanClass.class);
    assertEquals(String.class, reflector.getSetterType("id"));
  }


  @Test
  void shouldSettersWithUnrelatedArgTypesThrowException() {
    @SuppressWarnings("unused")
    class BeanClass {
      public void setTheProp(String arg) {}
      public void setTheProp(Integer arg) {}
    }
    when(reflectorFactory).findForClass(BeanClass.class);
    then(caughtException()).isInstanceOf(ReflectionException.class)
      .hasMessageContaining("theProp")
      .hasMessageContaining("BeanClass")
      .hasMessageContaining("java.lang.String")
      .hasMessageContaining("java.lang.Integer");
  }

  @Test
  void shouldAllowTwoBooleanGetters() throws Exception {
    @SuppressWarnings("unused")
    class Bean {
      // JavaBean Spec allows this (see #906)
      public boolean isBool() {return true;}
      public boolean getBool() {return false;}
      public void setBool(boolean bool) {}
    }
    Reflector reflector = reflectorFactory.findForClass(Bean.class);
    assertTrue((Boolean)reflector.getGetInvoker("bool").invoke(new Bean(), new Byte[0]));
  }

  @Test
  void testGetClassMethods() {
    Reflector reflector = reflectorFactory.findForClass(Child.class);
    Method[] classMethods = reflector.getClassMethods(Child.class);
    System.out.println(classMethods);
  }

  /**
   * 用默认构造方法，反射创建一个Student对象，反射获得studId属性并赋值为20，System.out输出studId的属性值。
   */
  @Test
  void test() throws InvocationTargetException, IllegalAccessException {
    ObjectFactory objectFactory = new DefaultObjectFactory();
    Student student = objectFactory.create(Student.class);
    Reflector reflector = new Reflector(Student.class);
    Invoker invoker = reflector.getSetInvoker("id");
    invoker.invoke(student, new Object[] { 20 });
    invoker = reflector.getGetInvoker("id");
    System.out.println("id=" + invoker.invoke(student, null));
  }

}
