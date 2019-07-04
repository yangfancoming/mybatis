
package org.apache.ibatis.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class TypeAliasRegistryTest {

  @Test
  void shouldRegisterAndResolveTypeAlias() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    typeAliasRegistry.registerAlias("rich", "org.apache.ibatis.domain.misc.RichType");

    assertEquals("org.apache.ibatis.domain.misc.RichType", typeAliasRegistry.resolveAlias("rich").getName());
  }

  @Test
  void shouldFetchArrayType() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    assertEquals(Byte[].class, typeAliasRegistry.resolveAlias("byte[]"));
  }

  @Test
  void shouldBeAbleToRegisterSameAliasWithSameTypeAgain() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    typeAliasRegistry.registerAlias("String", String.class);
    typeAliasRegistry.registerAlias("string", String.class);
  }

  @Test
  void shouldNotBeAbleToRegisterSameAliasWithDifferentType() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    assertThrows(TypeException.class, () -> typeAliasRegistry.registerAlias("string", BigDecimal.class));
  }

  @Test
  void shouldBeAbleToRegisterAliasWithNullType() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    assertNull(typeAliasRegistry.resolveAlias("foo"));
  }

  @Test
  void shouldBeAbleToRegisterNewTypeIfRegisteredTypeIsNull() {
    TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    typeAliasRegistry.registerAlias("foo", (Class<?>) null);
    typeAliasRegistry.registerAlias("foo", String.class);
  }

}
