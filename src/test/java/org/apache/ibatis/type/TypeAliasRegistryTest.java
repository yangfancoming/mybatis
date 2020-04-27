
package org.apache.ibatis.type;

import org.apache.ibatis.domain.misc.RichType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeAliasRegistryTest {

  TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

  @Test
  void test() {
    // 注册 Integer.class
    typeAliasRegistry.registerAlias("int","java.lang.Integer");
    // 注册 Long[].class 注意类名左边的[L和右边的分号（;） 这表示一个你指定类型的对象的数组
    typeAliasRegistry.registerAlias("long[]","[Ljava.lang.Long;");
    System.out.println(byte.class);
    System.out.println(Integer.class);
  }

  //  通过 全限定名串的方式注册别名   +  解析已注册的别名
  @Test
  void shouldRegisterAndResolveTypeAlias() {
    typeAliasRegistry.registerAlias("rich", "org.apache.ibatis.domain.misc.RichType");
    Class<RichType> rich = typeAliasRegistry.resolveAlias("rich");
    assertEquals("org.apache.ibatis.domain.misc.RichType", rich.getName());
  }

  // 测试 解析数组别名
  @Test
  void shouldFetchArrayType() {
    Class<Byte[]> objectClass = typeAliasRegistry.resolveAlias("byte[]");
    assertEquals(Byte[].class, objectClass);
  }

  //  别名相同 对应的类型也相同 则覆盖注册
  @Test
  void shouldBeAbleToRegisterSameAliasWithSameTypeAgain() {
    assertEquals(50,typeAliasRegistry.getTypeAliases().size());
    // 能够再次注册相同类型的别名。 其中 alias 参数 无论大小写 都会被转成小写存储  map会自动覆盖
    typeAliasRegistry.registerAlias("String", String.class);
    typeAliasRegistry.registerAlias("string", String.class);
    assertEquals(50,typeAliasRegistry.getTypeAliases().size());
  }

  //  别名相同 但对应的类型不同 则不能够注册
  @Test
  void shouldNotBeAbleToRegisterSameAliasWithDifferentType() {
    Class<String> string = typeAliasRegistry.resolveAlias("string");
    assertEquals(String.class, string);
    assertThrows(TypeException.class, () -> typeAliasRegistry.registerAlias("string", BigDecimal.class));
  }

  //  别名对应类型为null的  也可以注册
  @Test
  void shouldBeAbleToRegisterAliasWithNullType() {
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    assertNull(typeAliasRegistry.resolveAlias("foo"));
  }

  // 如果已经注册的 别名对应类型为null 则可以再次注册其他的类型
  @Test
  void shouldBeAbleToRegisterNewTypeIfRegisteredTypeIsNull() {
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    typeAliasRegistry.registerAlias("foo", String.class);
    assertEquals(String.class, typeAliasRegistry.resolveAlias("foo"));
  }

}
