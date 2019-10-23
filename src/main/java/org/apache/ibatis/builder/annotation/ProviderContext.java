
package org.apache.ibatis.builder.annotation;

import java.lang.reflect.Method;

/**
 * The context object for sql provider method.
 * @since 3.4.5
 */
public final class ProviderContext {

  private final Class<?> mapperType;
  private final Method mapperMethod;
  private final String databaseId;

  /**
   * Constructor.
   * @param mapperType A mapper interface type that specified provider
   * @param mapperMethod A mapper method that specified provider
   * @param databaseId A database id
   */
  ProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId) {
    this.mapperType = mapperType;
    this.mapperMethod = mapperMethod;
    this.databaseId = databaseId;
  }

  /**
   * Get a mapper interface type that specified provider.
   * @return A mapper interface type that specified provider
   */
  public Class<?> getMapperType() {
    return mapperType;
  }

  /**
   * Get a mapper method that specified provider.
   * @return A mapper method that specified provider
   */
  public Method getMapperMethod() {
    return mapperMethod;
  }

  /**
   * Get a database id that provided from {@link org.apache.ibatis.mapping.DatabaseIdProvider}.
   * @return A database id
   * @since 3.5.1
   */
  public String getDatabaseId() {
    return databaseId;
  }

}
