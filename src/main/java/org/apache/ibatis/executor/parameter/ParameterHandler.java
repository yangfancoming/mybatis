
package org.apache.ibatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A parameter handler sets the parameters of the {@code PreparedStatement}.
 */
public interface ParameterHandler {

  //获取参数 //该方法只在执行存储过程处理出参的时候被调用
  Object getParameterObject();
  //设置参数  //该方法在所有数据库方法设置SQL 参数时被调用
  void setParameters(PreparedStatement ps)
      throws SQLException;

}
