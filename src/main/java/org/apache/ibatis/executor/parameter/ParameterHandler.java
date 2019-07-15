
package org.apache.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A parameter handler sets the parameters of the {@code PreparedStatement}.
 *
 * getParameterObject是用来获取参数的，setParameters(PreparedStatement ps)是用来设置参数的，sos 相当于对sql中所有的参数都执行ps.setXXX(value);
 * ParameterHandler的默认实现类是DefaultParameterHandler，其实现了接口中定义的两个方法。

 */
public interface ParameterHandler {

  //获取参数 //该方法只在执行存储过程处理出参的时候被调用
  Object getParameterObject();
  //设置参数  //该方法在所有数据库方法设置SQL 参数时被调用
  void setParameters(PreparedStatement ps)
      throws SQLException;

}
