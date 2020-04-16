
package org.apache.ibatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * 利用JDK动态代理和责任链设计模式的综合运用
*/
public class Plugin implements InvocationHandler {

  //被代理的目标类
  private final Object target;
  //对应的拦截器
  private final Interceptor interceptor;
  //拦截器拦截的方法
  private final Map<Class<?>, Set<Method>> signatureMap;

  private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
    this.target = target;
    this.interceptor = interceptor;
    this.signatureMap = signatureMap;
  }

/**
 * 就以SqlCostPlugin为例，我的@Intercepts定义的是：
 * @Intercepts({
 * @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
 * @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
 * })
 *
 * 此时，生成的方法签名映射signatureMap应当是（我这里把Map给toString()了）：
 * {interface org.apache.ibatis.executor.statement.StatementHandler=[
 * public abstract int org.apache.ibatis.executor.statement.StatementHandler.update(java.sql.Statement) throws java.sql.SQLException,
 * public abstract java.util.List org.apache.ibatis.executor.statement.StatementHandler.query(java.sql.Statement,org.apache.ibatis.session.ResultHandler) throws java.sql.SQLException
 * ]}
 *
 * 一个Class对应一个Set，Class为StatementHandler.class，Set为StataementHandler中的两个方法
 *
 * 如果我new的是StatementHandler接口的实现类，那么可以为之生成代理，因为signatureMap中的key有StatementHandler这个接口
 * 如果我new的是Executor接口的实现类，那么直接会把Executor接口的实现类原样返回，因为signatureMap中的key并没有Executor这个接口
*/
  /**
   * @Description: 包装
   * @date 2019年10月27日21:11:26
   * @param target 要包装的目标对象 eg：RoutingStatementHandler
   * @param interceptor 指定要用哪个拦截器进行包装
   * @return 成功包装后的对象
   */
  public static Object wrap(Object target, Interceptor interceptor) {
    // 解析插件类上的 @Intercepts 注解
    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
    // 取得要改变行为的类(ParameterHandler|ResultSetHandler|StatementHandler|Executor)
    // 取得要包装的目标对象的类型
    Class<?> type = target.getClass();
    // 将target类所实现的所有接口的集合 与 @Intercepts中配置的 @Signature(type= StatementHandler.class)的集合 取出交集！
    Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
    // 使用jdk代理 代理接口的实现类，注意：一旦代理了 那么那些接口下的所有方法的调用都将会走 invoke方法
    if (interfaces.length > 0) {
      return Proxy.newProxyInstance(type.getClassLoader(),interfaces,new Plugin(target, interceptor, signatureMap));
    }
    return target;
  }

  /**
   *  就是将 @Intercepts 内容转换为 Map<Class<?>, Set<Method>>
   * @param interceptor   带有@Intercepts注解的Interceptor接口实现类
   * @see org.apache.ibatis.builder.ExamplePlugin
   * @return 填充好的 Map<Class<?>, Set<Method>> signatureMap
   */
  public static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) { //  -modify
    // 获取Interceptor类上的 @Intercepts 注解
    Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
    // issue #251 如果没有定义@Intercepts注解，抛出异常，这意味着使用MyBatis的插件，必须使用注解方式。
    if (interceptsAnnotation == null) throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
    // 解析Interceptor的values属性（Signature[]）数组，然后存入HashMap<Class<?>, Set< Method>>容器内
    Signature[] sigs = interceptsAnnotation.value();
    // 每个class里有多个Method需要被拦截,所以这么定义
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
    for (Signature sig : sigs) {
      // 1.先创建
      Set<Method> methods = signatureMap.computeIfAbsent(sig.type(), k -> new HashSet<>());
      try {
        // 通过方法名和参数  反射获取类中的指定方法
        Method method = sig.type().getMethod(sig.method(), sig.args());
        methods.add(method); // 2.再赋值
      } catch (NoSuchMethodException e) {
        throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
      }
    }
    return signatureMap;
  }

  /**
   * 该方法的实现比较简单，并不是获取目标对象所实现的所有接口，而是返回需要拦截的方法所包括的接口
  */
  public static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) { //  -modify
    Set<Class<?>> interfaces = new HashSet<>();
    while (type != null) {
      for (Class<?> c : type.getInterfaces()) {
        // 貌似只能拦截ParameterHandler|ResultSetHandler|StatementHandler|Executor
        // 拦截其他的无效
        // 当然我们可以覆盖Plugin.wrap方法，达到拦截其他类的功能
        if (signatureMap.containsKey(c)) {
          interfaces.add(c);
        }
      }
      type = type.getSuperclass();
    }
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

  /**
   * @date 2019年10月28日19:03:13
   * @param proxy 当前的代理对象
   * @param method 当前执行的方法
   * @param args 当前执行方法的参数
   * @return
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      // 通过当前执行方法 反射获取 其所属的类，
      Class<?> declaringClass = method.getDeclaringClass();
      // 再将类作为key 取出该类下 需要被拦截的方法集合
      Set<Method> methods = signatureMap.get(declaringClass);
      // 判断当前执行的方式 是否存在于需要被拦截的方法集合中，如果需被拦截的方法集合包含当前执行的方法，则执行拦截器的interceptor方法
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      // 最后还是执行原来逻辑
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
}
