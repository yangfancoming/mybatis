
package org.apache.ibatis.builder;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.util.Properties;

@Intercepts({
    @Signature(type= StatementHandler.class,method="parameterize",args=java.sql.Statement.class)
  })
public class ExamplePlugin implements Interceptor {

  private Properties properties;

  //实现拦截逻辑的地方，内部要通过invocation.proceed()显式地推进责任链前进，也就是调用下一个拦截器拦截目标方法。
  // 拦截目标对象中目标方法的执行
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    // 执行目标方法
    Object proceed = invocation.proceed();
    // 返回拦截之后的目标方法
    return proceed;
  }

  //用当前这个拦截器生成对目标target的代理，实际是通过Plugin.wrap(target,this) 来完成的，把目标target和拦截器this传给了包装函数。
  // 包装目标对象,即为目标对象创建一个代理对象
  @Override
  public Object plugin(Object target) {
    // target: 目标对象, interceptor: 拦截器, this 表示使用当前拦截器
    Object wrap = Plugin.wrap(target, this);
    return wrap;
  }

  //设置额外的参数，参数配置在拦截器的Properties节点里。
  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }

}
