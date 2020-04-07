
package org.apache.ibatis.session;

/**
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 * 指定 MyBatis 应如何自动映射列到字段或属性。
 * NONE ：禁用自动映射；
 * PARTIAL ：只会自动映射没有定义嵌套结果集映射的结果集。
 * FULL ：自动映射任意复杂的结果集（无论是否嵌套）。
 */
public enum AutoMappingBehavior {

  // Disables auto-mapping.
  NONE,

  // Will only auto-map results with no nested result mappings defined inside.
  PARTIAL,

  // Will auto-map result mappings of any complexity (containing nested or otherwise).
  FULL
}
