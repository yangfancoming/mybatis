
package org.apache.ibatis.scripting.defaults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 getParameterObject是获取参数，这个参数值就是你传递进来的值，可能是个实体、map或单个基本类型数据。
 重点看setParameters()，首先它读取了ParameterObject参数对象，然后用typeHandler对参数进行设置，而typeHandler里面需要对jdbcType和javaType进行处理，
 然后就设置参数了。也很好理解。所以当我们使用TypeHandler的时候完全可以控制如何设置SQL参数。
 设置参数，其实就是你在sql语句中配置的java对象和jdbc类型对应的关系，例如#{id,jdbcType=INTEGER}，id默认类型是javaType=class java.lang.Integer。
*/
public class DefaultParameterHandler implements ParameterHandler {

  // 记录Mybatis中全部的TypeHandler对象，用于参数类型转换（JdbcType -> JavaType 或 JavaType -> JbdcType）。
  private final TypeHandlerRegistry typeHandlerRegistry;

  // 记录SQL节点相应的配置信息
  private final MappedStatement mappedStatement;
  // 传入的参数对象
  private final Object parameterObject;//所有的参数值
  // 为Statement设置参数，就是根据BoundSql中记录的SQL语句创建的。
  private final BoundSql boundSql;
  private final Configuration configuration;

  public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    this.mappedStatement = mappedStatement;
    this.configuration = mappedStatement.getConfiguration();
    this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
    this.parameterObject = parameterObject;
    this.boundSql = boundSql;
  }

  @Override
  public Object getParameterObject() {
    return parameterObject;
  }

  @Override
  public void setParameters(PreparedStatement ps) {
    ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
    //获取所有参数，ParameterMapping是java类型和jdbc类型的对应关系 //NOTE: 获取SQL片段对应的ParameterMapping（与每个#{xxx}占位符都会生成一个ParameterMapping对象）
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    if (parameterMappings != null) {
      //遍历所有参数，将java 类型设置成jdbc类型
      for (int i = 0; i < parameterMappings.size(); i++) {
        ParameterMapping parameterMapping = parameterMappings.get(i);
        if (parameterMapping.getMode() != ParameterMode.OUT) {
          //参数值
          Object value;
          //获取参数名称
          String propertyName = parameterMapping.getProperty();
          if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            //获取参数值
            value = boundSql.getAdditionalParameter(propertyName);
          } else if (parameterObject == null) {
            value = null;
          } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) { //NOTE: 若有对应JavaType的TypeHandler，则直接返回parameterObject，因为可以直接通过TypeHandler转化为对应的jdbcType
            //如果是单个值则直接赋值
            value = parameterObject;
          } else {
            //NOTE: 可能会是对象，然后找对应属性的值，或者是一个Map则找对应key的value
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
          }
          //获取参数值对应的jdbc类型 //NOTE: 根据不同的typeHandler来设置参数值
          TypeHandler typeHandler = parameterMapping.getTypeHandler();
          JdbcType jdbcType = parameterMapping.getJdbcType();
          if (value == null && jdbcType == null) {
            jdbcType = configuration.getJdbcTypeForNull();
          }
          try {
            //设置参数值和jdbc类型的对应关系 //NOTE: 使用typeHandler 设置对应 #{} 位置的具体参数值
            typeHandler.setParameter(ps, i + 1, value, jdbcType);
          } catch (TypeException | SQLException e) {
            throw new TypeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
          }
        }
      }
    }
  }

}
