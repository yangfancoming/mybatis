
package org.apache.ibatis.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParamNameUtil {

  // -modify

  public static List<String> getParamNames(Method method) {
    return getParameterNames(method);
  }

  public static List<String> getParamNames(Constructor<?> constructor) {
    return getParameterNames(constructor);
  }

  // 最终重载函数
  private static List<String> getParameterNames(Executable executable) {
    List<String> collect = Arrays.stream(executable.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    return collect;
  }
}
