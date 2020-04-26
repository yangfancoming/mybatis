
package org.apache.ibatis.reflection;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.domain.misc.RichType;
import org.apache.ibatis.domain.misc.User;
import org.apache.ibatis.domain.misc.generics.GenericConcrete;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetaClassTest {

  ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
  MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);

  @Test
  void tes11t() {
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, reflectorFactory);
    System.out.println(metaConfig.hasSetter("阿道夫"));
    System.out.println(metaConfig.hasSetter("mapUnderscoreToCamelCase"));
  }

  @Test
  void shouldTestDataTypeOfGenericMethod() {
    MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
    assertEquals(Long.class, meta.getGetterType("id"));
    assertEquals(Long.class, meta.getSetterType("id"));
  }

  @Test
  void shouldThrowReflectionExceptionGetGetterType() {
    try {
      meta.getGetterType("aString");
      fail("should have thrown ReflectionException");
    } catch (ReflectionException expected) {
      assertEquals("There is no getter for property named \'aString\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
    }
  }

  @Test
  public void test(){
    MetaClass meta = MetaClass.forClass(User.class, reflectorFactory);
    assertTrue(meta.hasGetter("id"));
    assertTrue(meta.hasSetter("id"));

    assertTrue(meta.hasSetter("name"));
    assertTrue(meta.hasGetter("name"));

    assertFalse(meta.hasGetter("what"));
  }

  @Test
  void shouldCheckGetterExistance() {
    assertTrue(meta.hasGetter("richField"));
    assertTrue(meta.hasGetter("richProperty"));
    assertTrue(meta.hasGetter("richList"));
    assertTrue(meta.hasGetter("richMap"));
    assertTrue(meta.hasGetter("richList[0]"));

    assertTrue(meta.hasGetter("richType"));
    assertTrue(meta.hasGetter("richType.richField"));
    assertTrue(meta.hasGetter("richType.richProperty"));
    assertTrue(meta.hasGetter("richType.richList"));
    assertTrue(meta.hasGetter("richType.richMap"));
    assertTrue(meta.hasGetter("richType.richList[0]"));

    assertEquals("richType.richProperty", meta.findProperty("richType.richProperty", false));
    assertFalse(meta.hasGetter("[0]"));
  }

  @Test
  void shouldCheckSetterExistance() {
    assertTrue(meta.hasSetter("richField"));
    assertTrue(meta.hasSetter("richProperty"));
    assertTrue(meta.hasSetter("richList"));
    assertTrue(meta.hasSetter("richMap"));
    assertTrue(meta.hasSetter("richList[0]"));

    assertTrue(meta.hasSetter("richType"));
    assertTrue(meta.hasSetter("richType.richField"));
    assertTrue(meta.hasSetter("richType.richProperty"));
    assertTrue(meta.hasSetter("richType.richList"));
    assertTrue(meta.hasSetter("richType.richMap"));
    assertTrue(meta.hasSetter("richType.richList[0]"));

    assertFalse(meta.hasSetter("[0]"));
  }

  @Test
  void shouldCheckTypeForEachGetter() {
    assertEquals(String.class, meta.getGetterType("richField"));
    assertEquals(String.class, meta.getGetterType("richProperty"));
    assertEquals(List.class, meta.getGetterType("richList"));
    assertEquals(Map.class, meta.getGetterType("richMap"));
    assertEquals(List.class, meta.getGetterType("richList[0]"));

    assertEquals(RichType.class, meta.getGetterType("richType"));
    assertEquals(String.class, meta.getGetterType("richType.richField"));
    assertEquals(String.class, meta.getGetterType("richType.richProperty"));
    assertEquals(List.class, meta.getGetterType("richType.richList"));
    assertEquals(Map.class, meta.getGetterType("richType.richMap"));
    assertEquals(List.class, meta.getGetterType("richType.richList[0]"));
  }

  @Test
  void shouldCheckTypeForEachSetter() {
    assertEquals(String.class, meta.getSetterType("richField"));
    assertEquals(String.class, meta.getSetterType("richProperty"));
    assertEquals(List.class, meta.getSetterType("richList"));
    assertEquals(Map.class, meta.getSetterType("richMap"));
    assertEquals(List.class, meta.getSetterType("richList[0]"));

    assertEquals(RichType.class, meta.getSetterType("richType"));
    assertEquals(String.class, meta.getSetterType("richType.richField"));
    assertEquals(String.class, meta.getSetterType("richType.richProperty"));
    assertEquals(List.class, meta.getSetterType("richType.richList"));
    assertEquals(Map.class, meta.getSetterType("richType.richMap"));
    assertEquals(List.class, meta.getSetterType("richType.richList[0]"));
  }

  @Test
  void shouldCheckGetterAndSetterNames() {
    assertEquals(5, meta.getGetterNames().length);
    assertEquals(5, meta.getSetterNames().length);
  }

  @Test
  void shouldFindPropertyName() {
    assertEquals("richField", meta.findProperty("RICHfield"));
  }

}
