
package org.apache.ibatis.type;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TypeAliasRegistryTest {

  TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

  //  注册别名 map 和 取出别名 map 操作
  @Test
  void shouldRegisterAndResolveTypeAlias() {
    typeAliasRegistry.registerAlias("rich", "org.apache.ibatis.domain.misc.RichType");
    Class<Object> rich = typeAliasRegistry.resolveAlias("rich");
    System.out.println(rich.getName());
    assertEquals("org.apache.ibatis.domain.misc.RichType", typeAliasRegistry.resolveAlias("rich").getName());
  }

  @Test
  void shouldFetchArrayType() {
    Class<Object> objectClass = typeAliasRegistry.resolveAlias("byte[]");
    System.out.println(objectClass); // class [Ljava.lang.Byte;
    assertEquals(Byte[].class, typeAliasRegistry.resolveAlias("byte[]"));
  }

  @Test
  void shouldBeAbleToRegisterSameAliasWithSameTypeAgain() {
    // 能够再次注册相同类型的别名     其中 alias 参数 无论大小写进源码后 都会被转成小写存储
    typeAliasRegistry.registerAlias("String", String.class);
    typeAliasRegistry.registerAlias("string", String.class);
  }

  @Test
  void shouldNotBeAbleToRegisterSameAliasWithDifferentType() {
    //  别名相同 但对应的类型不同 则不能够注册
    assertThrows(TypeException.class, () -> typeAliasRegistry.registerAlias("string", BigDecimal.class));
  }

  @Test
  void shouldBeAbleToRegisterAliasWithNullType() {
    //  别名对应类型为null的  也可以注册
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    System.out.println(typeAliasRegistry.resolveAlias("foo"));// null
    assertNull(typeAliasRegistry.resolveAlias("foo"));
  }

  @Test
  void shouldBeAbleToRegisterNewTypeIfRegisteredTypeIsNull() {
    // 如果已经注册的 别名对应类型为null 则可以再次注册新的类型
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    typeAliasRegistry.registerAlias("foo", String.class);
    System.out.println(typeAliasRegistry.resolveAlias("foo"));//  class java.lang.String
  }

}
