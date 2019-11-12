
package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * ParamNameResolver 为 sql 语句参数解析器
 * 主要用来处理接口形式的参数，最后会把参数处放在一个map中
 * map的key为参数的位置，value为参数的名字
 */
public class ParamNameResolver {

  private static final String GENERIC_NAME_PREFIX = "param";

  /**
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   *
   * 对names字段的解释
   * - Method(@Param("M") int a, @Param("N") int b)转化为map为{{0, "M"}, {1, "N"}}
   * - Method(int a, int b)转化为map为{{0, "0"}, {1, "1"}}
   * - aMethod(int a, RowBounds rb, int b)转化为map为{{0, "0"}, {2, "1"}}
   */
  // 存放参数的位置和对应的参数名 在本类的构造函数中创建
  private final SortedMap<Integer, String> names;
  //是否使用 @param 注解
  private boolean hasParamAnnotation;

  public ParamNameResolver(Configuration config, Method method) {
    // 通过反射得到方法的参数类型
    final Class<?>[] paramTypes = method.getParameterTypes();
    // 获取参数注解  返回的是注解的二维数组，每一个方法的参数包含一个注解数组。
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      // 首先判断这个参数的类型是否是特殊类型,RowBounds ResultHandler，是的话跳过，咱不处理
      if (isSpecialParameter(paramTypes[paramIndex])) {
        continue;
      }
      String name = null;
      // 如果此次查询使用的是注解
      // 判断这个参数是否是用了@Param注解，如果使用的话name就是@Param注解的值，并把name放到map中，键为参数在方法中的位置，value为Param的值
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
      // 如果没有使用Param注解,判断是否开启了UseActualParamName，如果开启了，则使用java8的反射得到方法的名字，此处容易造成异常
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
   * Returns parameter names referenced by SQL providers. GOAT
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
      /** 2.如果没有@Param注解 并且 只有1个参数  则直接返回SortedMap集合的第一个元素。 即：单个参数 直接返回 */
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
