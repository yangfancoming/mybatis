package org.apache.goat.chapter200.D10;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.util.Properties;

@Intercepts(
		{
			@Signature(type= StatementHandler.class,method="parameterize",args=java.sql.Statement.class)
		})
public class MySecondPlugin implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		System.out.println("MySecondPlugin...intercept:" + invocation.getMethod());
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		// TODO Auto-generated method stub
		System.out.println("MySecondPlugin...plugin:" + target);
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
	}

}
