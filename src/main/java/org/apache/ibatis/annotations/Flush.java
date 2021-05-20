
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * The maker annotation that invoke a flush statements via Mapper interface.
 * @Flush  注解，如果使用了这个注解，定义在 Mapper 接口中的方法能够调用 SqlSession#flushStatements() 方法
 * @since 3.3.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flush {
}
