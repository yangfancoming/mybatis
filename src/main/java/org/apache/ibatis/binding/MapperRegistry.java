
package org.apache.ibatis.binding;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 代理模式 （jdk动态代理）优点： 再不修改源码的基础上，对已有方法进行增强。
 * MapperRegistry 是 Mapper 接口及其对应的代理对象工厂 的注册中心。
 * 注册和获取Mapper对象的代理
 * 这里关注 Configuration.mapperRegistry 字段，它记录当前使用的 MapperRegistry 对象
 * */
public class MapperRegistry {

  private final Configuration config;

  /**
   * 记录了 Mapper 接口与对应 MapperProxyFactory 之间的关系
   * 该集合的 key 是Mapper 接口对应的 Class 对象， value 为 MapperProxyFactory 工厂对象，可以为 Mapper 接口创建代理对象
  */
  private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

  public MapperRegistry(Configuration config) {
    this.config = config;
  }

  //返回代理类
  @SuppressWarnings("unchecked")
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    // 从 knownMappers 中获取与 type 对应的 MapperProxyFactory
    final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
    if (mapperProxyFactory == null) { //说明这个Mapper接口没有注册
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      //生成一个MapperProxy对象
      return mapperProxyFactory.newInstance(sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }

  public <T> boolean hasMapper(Class<T> type) {
    return knownMappers.containsKey(type);
  }

  /**
   * @Description: 包装
   * @date 2019年10月27日21:11:26
   * @param type interface org.apache.goat.chapter100.C010.EmployeeMapper
   * @param type interface org.apache.goat.chapter100.A044.ZooMapper
   * @param type class org.apache.goat.chapter100.A044.App
   * @return
   * 这里同样是扫描指定包路径地下的所有类，并且根据filter（new ResolverUtil.IsA(superType)），
   * 挑选出满足条件的类，这里的条件是Object.class，所以包底下的所有类都会被装进来，
   * 接下来就是遍历这些类然后解析了
   */
  public <T> void addMapper(Class<T> type) {
    // 忽略掉所有非接口类，mapper必须是接口类 interface才会被添加！
    if (!type.isInterface()) return; // -modify
    //检测是否已经加载过该接口，如果重复添加了，抛出绑定异常
    if (hasMapper(type)) {
      throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
    }
    boolean loadCompleted = false;
    try {
      //将mapper接口包装成mapper代理 interface org.apache.ibatis.zgoat.A03.FooMapper
      MapperProxyFactory<T> tMapperProxyFactory = new MapperProxyFactory<>(type);
      // 该集合的 key 是Mapper 接口对应的 Class 对象， value 为 MapperProxyFactory 工厂对象 可以为 Mapper 接口创建代理对象
      knownMappers.put(type, tMapperProxyFactory);
      /**  验证 两个对象相同
       Class<T> mapperInterface = tMapperProxyFactory.getMapperInterface();
       System.out.println(type == mapperInterface); // true
       System.out.println(type.equals(mapperInterface) ); // true
       */
      /**
       It's important that the type is added before the parser is run otherwise the binding may automatically be attempted by the mapper parser. If the type is already known, it won't try.
       在运行分析器之前添加类型很重要。 否则，绑定可能会被 映射器分析器。如果类型已知，则不会尝试。
       解析接口上的注解或者加载mapper配置文件生成mappedStatement
       */
      //这里就是关键处理类了，对可能存在注解的MapperInterface接口进行处理
      MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
      parser.parse();
      loadCompleted = true;
    } finally {
      //如果加载过程中出现异常需要再将这个mapper从mybatis中删除,这种方式比较丑陋吧，难道是不得已而为之？
      if (!loadCompleted) {
        knownMappers.remove(type);
      }
    }
  }

  /**
   * @since 3.2.2
   */
  public Collection<Class<?>> getMappers() {
    return Collections.unmodifiableCollection(knownMappers.keySet());
  }

  /**
   * @since 3.2.2
   * 查找包下所有类
   * @param packageName org.apache.goat.chapter100.A044
   */
  public void addMappers(String packageName) {
    addMappers(packageName, Object.class);
  }

  /**
   * @since 3.2.2
   * @param packageName  org.apache.goat.chapter100.A.A044
   * @param superType    Object.class
   * 根据注解生成mappedstatemnet：
   * configuration会把工作委托给MapperRegistry去做，MapperRegistry会持有所有被解析的接口（运行时生成动态代理用），
   * 而最终解析的产物：mappedstatement依然会被configuration实例持有放在mappedStatements的map中：
   */
  public void addMappers(String packageName, Class<?> superType) {
    //查找包下所有是superType的类
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    // 给 ResolverUtil类的 matches 赋值
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    // 从 ResolverUtil类的 matches 中获取
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    /**
     * mapperSet 集合：
     * interface org.apache.goat.chapter100.A044.ZooMapper
     * class org.apache.goat.chapter100.A044.App
     * interface org.apache.goat.chapter100.A044.FooMapper
    */
    for (Class<?> mapperClass : mapperSet) {
      addMapper(mapperClass);
    }
  }

}
