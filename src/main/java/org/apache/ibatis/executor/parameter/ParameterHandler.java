
package org.apache.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A parameter handler sets the parameters of the {@code PreparedStatement}.
 * ParameterHandler 在Mybatis四大对象中负责将sql中的占位符替换为真正的参数，它是一个接口，有且只有一个实现类DefaultParameterHandler
 * getParameterObject是用来获取参数的，setParameters(PreparedStatement ps)是用来设置参数的，sos 相当于对sql中所有的参数都执行ps.setXXX(value);
 * ParameterHandler的默认实现类是DefaultParameterHandler，其实现了接口中定义的两个方法。
 * 其中 setParameters 是处理参数最核心的方法
 */
public interface ParameterHandler {

  //获取参数 //该方法只在执行存储过程处理出参的时候被调用
  Object getParameterObject();
  //设置参数  //该方法在所有数据库方法设置SQL 参数时被调用
  void setParameters(PreparedStatement ps)  throws SQLException;


}
