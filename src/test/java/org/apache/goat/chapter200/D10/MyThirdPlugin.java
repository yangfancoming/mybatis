package org.apache.goat.chapter200.D10;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;


@Intercepts(
		{
//			@Signature(type= StatementHandler.class,method="parameterize",args=java.sql.Statement.class),
      @Signature(type = StatementHandler.class, method = "update", args = {java.sql.Statement.class}),
//      @Signature(type = StatementHandler.class, method = "query", args = {java.sql.Statement.class, ResultHandler.class}),
//      @Signature(type = StatementHandler.class, method = "batch", args = { java.sql.Statement.class })
		})
public class MyThirdPlugin implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		System.out.println("MyThirdPlugin...intercept:" + invocation.getMethod());
		return invocation.proceed();
	}

}
