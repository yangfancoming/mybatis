
package org.apache.ibatis.annotations;

import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;

import java.lang.annotation.*;

/**
 缓存的操作可选项
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Options {
  /**
   * The options for the {@link Options#flushCache()}.
   * The default is {@link FlushCachePolicy#DEFAULT}
   */
  enum FlushCachePolicy {
    /** <code>false</code> for select statement; <code>true</code> for insert/update/delete statement. */
    DEFAULT,
    /** Flushes cache regardless of the statement type. */
    TRUE,
    /** Does not flush cache regardless of the statement type. */
    FALSE
  }

  boolean useCache() default true;

  FlushCachePolicy flushCache() default FlushCachePolicy.DEFAULT;

  ResultSetType resultSetType() default ResultSetType.DEFAULT;

  StatementType statementType() default StatementType.PREPARED;

  int fetchSize() default -1;

  int timeout() default -1;

  boolean useGeneratedKeys() default false;

  String keyProperty() default "";

  String keyColumn() default "";

  String resultSets() default "";
}
