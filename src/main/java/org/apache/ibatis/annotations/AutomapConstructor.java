
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
 * The marker annotation that indicate a constructor for automatic mapping.
 * 在与数据库查询结果对应的model类的构造器上增加这个注释，可以让框架自动使用这个构造器。
 * @since 3.4.3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface AutomapConstructor {
}
