
package org.apache.ibatis.type;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.*;

/**
 TypeAliasRegistry（类型别名注册器）
 在类型别名注册器类TypeAliasRegistry的无参构造器中进行了大量的基础类型别名的注册，涉及到的有：
 　　　　1.字符串类型（别名类似string）
 　　　　2.基本类型包装器类型及其数组类型（别名类似byte、byte[]）
 　　　　3.基本类型及其数组类型（别名类似_byte、_byte[]）
 　　　　4.日期类型及其数组类型（别名类似date、date[]）
 　　　　5.大数字类型及其数组类型（别名类似bigdecimal、bigdecimal[]）
 　　　　6.Object类型及其数组类型（别名类似object、object[]）
 　　　　7.集合类型（别名类似collection、map、list、hsahmap、arraylist、iterator）
 　　　　8.ResultSet结果集类型（别名为ResultSet）
 　注意：这并不是全部的MyBatis内置的类型别名，还有一部分类型别名是在创建Configuration实例的时候在其无参构造器中进行注册的
*/
public class TypeAliasRegistry {

  private static final Log log = LogFactory.getLog(TypeAliasRegistry.class);

  // key为别名， value就是别名对应的类型（class对象）
  private final Map<String, Class<?>> typeAliases = new HashMap<>();

  // 注册系统内置的类型别名
  public TypeAliasRegistry() {
    log.warn("进入 【TypeAliasRegistry】 无参 构造函数 {}");
    // 字符串类型
    registerAlias("string", String.class);
    // 基本包装类型
    registerAlias("byte", Byte.class);
    registerAlias("long", Long.class);
    registerAlias("short", Short.class);
    registerAlias("int", Integer.class);
    registerAlias("integer", Integer.class);
    registerAlias("double", Double.class);
    registerAlias("float", Float.class);
    registerAlias("boolean", Boolean.class);
    // 基本数组包装类型
    registerAlias("byte[]", Byte[].class);
    registerAlias("long[]", Long[].class);
    registerAlias("short[]", Short[].class);
    registerAlias("int[]", Integer[].class);
    registerAlias("integer[]", Integer[].class);
    registerAlias("double[]", Double[].class);
    registerAlias("float[]", Float[].class);
    registerAlias("boolean[]", Boolean[].class);
    // 加个下划线，就变成了基本类型
    registerAlias("_byte", byte.class);
    registerAlias("_long", long.class);
    registerAlias("_short", short.class);
    registerAlias("_int", int.class);
    registerAlias("_integer", int.class);
    registerAlias("_double", double.class);
    registerAlias("_float", float.class);
    registerAlias("_boolean", boolean.class);
    // 加个下划线，就变成了基本数组类型
    registerAlias("_byte[]", byte[].class);
    registerAlias("_long[]", long[].class);
    registerAlias("_short[]", short[].class);
    registerAlias("_int[]", int[].class);
    registerAlias("_integer[]", int[].class);
    registerAlias("_double[]", double[].class);
    registerAlias("_float[]", float[].class);
    registerAlias("_boolean[]", boolean[].class);
    // 日期数字类型
    registerAlias("date", Date.class);
    registerAlias("decimal", BigDecimal.class);
    registerAlias("bigdecimal", BigDecimal.class);
    registerAlias("biginteger", BigInteger.class);
    registerAlias("object", Object.class);
    // 日期数字数组类型
    registerAlias("date[]", Date[].class);
    registerAlias("decimal[]", BigDecimal[].class);
    registerAlias("bigdecimal[]", BigDecimal[].class);
    registerAlias("biginteger[]", BigInteger[].class);
    registerAlias("object[]", Object[].class);
    // 集合型
    registerAlias("map", Map.class);
    registerAlias("hashmap", HashMap.class);
    registerAlias("list", List.class);
    registerAlias("arraylist", ArrayList.class);
    registerAlias("collection", Collection.class);
    registerAlias("iterator", Iterator.class);
    registerAlias("ResultSet", ResultSet.class);
  }

  /**
   * 解析别名   全局唯一出口
   * @param string
   * throws class cast exception as well if types cannot be assigned
  */
  @SuppressWarnings("unchecked")
  public <T> Class<T> resolveAlias(String string) {
    if (string == null) return null;
    try {
      /**
       * issue #748
       * 先转成小写再解析 这里转个小写也有bug？见748号bug(在google code上)
       * https://code.google.com/p/mybatis/issues
       * 比如 如果本地语言是Turkish，那i转成大写就不是I了，而是另外一个字符（İ）。这样土耳其的机器就用不了mybatis了！这是一个很大的bug，但是基本上每个人都会犯......
      */
      String key = string.toLowerCase(Locale.ENGLISH);
      Class<T> value;
      // 原理就很简单了，从HashMap里找对应的键值，找到则返回类型别名对应的Class
      if (typeAliases.containsKey(key)) {
        value = (Class<T>) typeAliases.get(key);
      } else {
        // 找不到，就再试着 反射创建类的实例 (这样怪不得我们也可以直接用java.lang.Integer的方式定义，也可以就int这么定义)
        value = (Class<T>) Resources.classForName(string);
      }
      return value;
    } catch (ClassNotFoundException e) {
      throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
    }
  }

  /**
   * 根据包名，批量注册别名
   * 使用Object.class作为父类  调用重载函数
   * @param packageName  <package name="org.apache.goat.common"/> 标签中的 org.apache.goat.common
  */
  public void registerAliases(String packageName) {
    registerAliases(packageName, Object.class);
  }

  public void registerAliases(String packageName, Class<?> superType) {
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // 将指定packageName下的所有class文件，全部添加到 ResolverUtil.matches 中
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    // 添加完成后再取出来
    Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
    // 取出来后，再遍历进行注册
    for (Class<?> type : typeSet) {
      // Ignore inner classes and interfaces (including package-info.java)  Skip also inner classes. See issue #6
      // 忽略 匿名类、接口、成员内部类
      if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
        registerAlias(type);
      }
    }
  }

  public void registerAlias(Class<?> type) {
    String alias = type.getSimpleName();
    // 判断 @Alias("what")
    Alias aliasAnnotation = type.getAnnotation(Alias.class);
    // 面试题  mybatis 配置别名的三种方式中 优先级最高的为 @Alias("what") 注解！
    if (aliasAnnotation != null)  alias = aliasAnnotation.value();
    registerAlias(alias, type);
  }

  /**
   * 注册别名功能
   * @param alias - 要注册的别名 eg: "rich"
   * @param value - 全限定类名   eg: "org.apache.ibatis.domain.misc.RichType"
   */
  public void registerAlias(String alias, String value) {
    try {
      registerAlias(alias, Resources.classForName(value));
    } catch (ClassNotFoundException e) {
      throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + e, e);
    }
  }

  // 最终重载函数
  public void registerAlias(String alias, Class<?> value) {
    if (alias == null)  throw new TypeException("The parameter alias cannot be null");
    // issue #748
    String key = alias.toLowerCase(Locale.ENGLISH);
    /**
     * 如果已经存在key了，且value和之前不一致则报错，代码执行流程如下：
     *  <typeAlias type="org.apache.goat.common.model.Bar" alias="foo"/>
     *      创建k,v键值对（foo , org.apache.goat.common.model.Bar） ok
     *  <package name="org.apache.goat.common"/>
     *      创建k,v键值对（bar , org.apache.goat.common.model.Bar） ok
     *      创建k,v键值对（foo , org.apache.goat.common.model.Foo） error  The alias 'Foo' is already mapped to the value 'org.apache.goat.common.model.Bar'.
     *  mybatis别名原则： 不同的key可以对应同一个value，但是相同key的不同value是不允许被覆盖的！(map是允许覆盖的)
     *  相对于只判断key是否存在(Map)而言，后者在相同key和吧不同value的情况下(typeAliases)会报异常，不允许覆盖
     */
    if (typeAliases.containsKey(key) && typeAliases.get(key) != null && !typeAliases.get(key).equals(value)) {
      throw new TypeException("The alias '" + alias + "' is already mapped to the value '" + typeAliases.get(key).getName() + "'.");
    }
    typeAliases.put(key, value);
  }

  /** @since 3.2.2 */
  public Map<String, Class<?>> getTypeAliases() {
    return Collections.unmodifiableMap(typeAliases);
  }
}
