
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

/**注册和获取Mapper对象的代理 */
public class MapperRegistry {

  // Configuration 对象， MyBatis 全局唯一的配置对象，其中包含了所有配置信息
  private final Configuration config;

  // 记录了 Mapper 接口与对应 MapperProxyFactory 之间的关系
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
   * @author fan.yang
   * @date 2019年10月27日21:11:26
   * @param type interface org.apache.goat.chapter100.C010.EmployeeMapper
   * @return
   *
   * 这里同样是扫描指定包路径地下的所有类，并且根据filter（new ResolverUtil.IsA(superType)），
   * 挑选出满足条件的类，这里的条件是Object.class，所以包底下的所有类都会被装进来，
   * 接下来就是遍历这些类然后解析了
   */
  public <T> void addMapper(Class<T> type) {
    if (type.isInterface()) { // 判断该类是否是 接口类 interface  //mapper必须是接口！才会添加
      if (hasMapper(type)) {  //如果重复添加了，报错
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
         It's important that the type is added before the parser is run
         otherwise the binding may automatically be attempted by the
         mapper parser. If the type is already known, it won't try.
         在运行分析器之前添加类型很重要。 否则，绑定可能会被 映射器分析器。如果类型已知，则不会尝试
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
  }

  /**
   * @since 3.2.2
   */
  public Collection<Class<?>> getMappers() {
    return Collections.unmodifiableCollection(knownMappers.keySet());
  }

  /**
   * @since 3.2.2
   * 根据注解生成mappedstatemnet：
   * configuration会把工作委托给MapperRegistry去做，MapperRegistry会持有所有被解析的接口（运行时生成动态代理用），
   * 而最终解析的产物：mappedstatement依然会被configuration实例持有放在mappedStatements的map中：
   */
  public void addMappers(String packageName, Class<?> superType) {
    //查找包下所有是superType的类
    ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
    resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
    Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
    for (Class<?> mapperClass : mapperSet) {
      addMapper(mapperClass);
    }
  }

  /**
   * @since 3.2.2
   * 查找包下所有类
   */
  public void addMappers(String packageName) {
    addMappers(packageName, Object.class);
  }

}
