
package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class ParamNameResolver {

  private static final String GENERIC_NAME_PREFIX = "param";

  /**
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   *
   */
  // 存放参数的位置和对应的参数名 在本类的构造函数中创建
  private final SortedMap<Integer, String> names;
  //是否使用param注解
  private boolean hasParamAnnotation;

  public ParamNameResolver(Configuration config, Method method) {
    // 获取参数类型列表
    final Class<?>[] paramTypes = method.getParameterTypes();
    // 获取参数注解
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      // 检测当前的参数类型是否为 RowBounds 或 ResultHandler
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        continue;
      }
      String name = null;
      // 如果此次查询使用的是注解
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {
          // 标记 此次查询方式 是使用的注解
          hasParamAnnotation = true;
          // 获取 @Param 注解内容
          name = ((Param) annotation).value();
          break;
        }
      }
      // 如果此次查询方式 使用的不是注解 （ @Param was not specified.）
      if (name == null) {
        if (config.isUseActualParamName()) {
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          name = String.valueOf(map.size());
        }
      }
      // #存入参数  每次循环解析 都会给map中 添加 索引和
      map.put(paramIndex, name); // put 进去 0,arg0
    }
    /**
     * {@link org.apache.goat.chapter100.C010.App3} getEmpByIdAndLastName3    @Param 传参
     * "0" -> "id"   "1" -> "lastName"
     * {@link org.apache.goat.chapter100.C010.App5} getEmpByIdAndLastName5    Map 传参
     * "0" -> "arg0"
     */
    names = Collections.unmodifiableSortedMap(map); //
  }

  private String getActualParamName(Method method, int paramIndex) {
    List<String> paramNames = ParamNameUtil.getParamNames(method);
    String s = paramNames.get(paramIndex);
    return s;
  }

  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * Returns parameter names referenced by SQL providers.
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * A single non-special parameter is returned without a name.
   * 单个非特殊参数没有名称
   * Multiple parameters are named using the naming rule.
   * 多个参数 使用命名规则命名
   * In addition to the default names, this method also adds the generic names (param1, param2,...).
   *
   *   Author selectAuthForBlog(@Param("id") Integer id,@Param("name") String name );
   *   传入的参数是[1,"张三"]
   *   最后解析的map为{"id":"1","name":"张三"}
   */
  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    /** 1.如果参数为空 则直接返回 */
    if (args == null || paramCount == 0) {
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) {
      /**
       *  2.如果没有@Param注解 并且 只有1个参数  则直接返回SortedMap集合的第一个元素
       *  即：单个参数 直接返回
       */
      return args[names.firstKey()];
    } else {
      /**
       * 3.有@Param注解 或 多个参数的情况 则封装 map
       * "0" -> "id"
       * "1" -> "lastName"
       */
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        // "id" -> "15"  //1.先加一个#{0},#{1},#{2}...参数
        param.put(entry.getValue(), args[entry.getKey()]);

        /**
         *  add generic param names (param1, param2, ...)
         *  额外的将每一个参数也保存到map中，使用新的key：param1...paramN
         *  效果：有Param注解可以#{指定的key}，或者#{param1}
        */
        final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);// param1
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          //2.再加一个#{param1},#{param2}...参数
          //你可以传递多个参数给一个映射器方法。如果你这样做了,
          //默认情况下它们将会以它们在参数列表中的位置来命名,比如:#{param1},#{param2}等。
          //如果你想改变参数的名称(只在多参数情况下) ,那么你可以在参数上使用@Param(“paramName”)注解。
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
