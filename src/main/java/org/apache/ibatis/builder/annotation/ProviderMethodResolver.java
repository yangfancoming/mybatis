

package org.apache.ibatis.builder.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.builder.BuilderException;

/**
 * The interface that resolve an SQL provider method via an SQL provider class.
 * 通过SQL提供程序类解析SQL提供程序方法的接口
 * This interface need to implements at an SQL provider class and it need to define the default constructor for creating a new instance.
 * 此接口需要在SQL提供程序类中实现，并且需要定义用于创建新实例的默认构造函数
 * @since 3.5.1
 */
public interface ProviderMethodResolver {

  /**
   * Resolve an SQL provider method.
   * The default implementation return a method that matches following conditions.
   * Method name matches with mapper method Return type matches the {@link CharSequence}({@link String}, {@link StringBuilder}, etc...)
   * If matched method is zero or multiple, it throws a {@link BuilderException}.
   * @param context a context for SQL provider
   * @return an SQL provider method
   * @throws BuilderException Throws when cannot resolve a target method
   */
  default Method resolveMethod(ProviderContext context) {
    List<Method> sameNameMethods = Arrays.stream(getClass().getMethods())
        .filter(m -> m.getName().equals(context.getMapperMethod().getName())).collect(Collectors.toList());

    if (sameNameMethods.isEmpty()) {
      throw new BuilderException("Cannot resolve the provider method because '"+ context.getMapperMethod().getName() + "' not found in SqlProvider '" + getClass().getName() + "'.");
    }
    List<Method> targetMethods = sameNameMethods.stream()
        .filter(m -> CharSequence.class.isAssignableFrom(m.getReturnType())).collect(Collectors.toList());

    if (targetMethods.size() == 1) {
      return targetMethods.get(0);
    }
    if (targetMethods.isEmpty()) {
      throw new BuilderException("Cannot resolve the provider method because '"
          + context.getMapperMethod().getName() + "' does not return the CharSequence or its subclass in SqlProvider '" + getClass().getName() + "'.");

    } else {
      throw new BuilderException("Cannot resolve the provider method because '"
          + context.getMapperMethod().getName() + "' is found multiple in SqlProvider '" + getClass().getName() + "'.");
    }
  }

}
