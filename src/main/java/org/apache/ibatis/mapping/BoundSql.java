
package org.apache.ibatis.mapping;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An actual SQL String got from an {@link SqlSource} after having processed any dynamic content.
 * The SQL may have SQL placeholders "?" and an list (ordered) of an parameter mappings
 * with the additional information for each parameter (at least the property name of the input object to read the value from).
 * Can also have additional parameters that are created by the dynamic language (for loops, bind...).
 *  其中包含sql语句(该sql语句中可能包含 ? 这样的占位符), 以及一组parameter mapping(ParameterMapping类的实例), 注意这组parameter mapping是Mybatis内部生成的(通过读取#{xx}中的内容)
 *  再强调一次,以上的ParameterMapping实例是在ParameterHandler接口的唯一默认实现类 DefaultParameterHandler 中被消费的.
 */
public class BoundSql {

  // 进行 #{ } 和 ${ } 替换完毕之后的结果sql, 注意每个 #{ }替换完之后就是一个 ?
  private final String sql; // select * from foo where id = ?

  // 这里的parameterMappings列表参数里的item个数, 以及每个item的属性名称等等, 都是和上面的sql中的 ? 完全一一对应的.
  private final List<ParameterMapping> parameterMappings; // ParameterMapping{property='id', mode=IN, javaType=class java.lang.Integer, jdbcType=null, numericScale=null, resultMapId='null', jdbcTypeName='null', expression='null'}

  // 用户传入的数据
  private final Object parameterObject; // 2

  private final Map<String, Object> additionalParameters;

  private final MetaObject metaParameters;

  public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
    //sql : SELECT us_code FROM xxx WHERE us_code = ? AND us_keyid IS NOT NULL
    // 到达这里的sql(即用来构建BoundSql实例的sql), 是进行 #{ } 和 ${ } 替换完毕之后的结果sql
    this.sql = sql;
    // parameterMappings : [ParameterMapping{property='username', mode=IN, javaType=class java.lang.Object, jdbcType=null, numericScale=null, resultMapId='null', jdbcTypeName='null', expression='null'}]
    // 这里的parameterMappings列表参数里的item个数, 以及每个item的属性名称等等, 都是和上面的sql中的 ? 完全一一对应的.
    this.parameterMappings = parameterMappings;
    // parameterObject : 用户传入的数据
    this.parameterObject = parameterObject;
    this.additionalParameters = new HashMap<>();
    this.metaParameters = configuration.newMetaObject(additionalParameters);
  }

  public String getSql() {
    return sql;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

  public Object getParameterObject() {
    return parameterObject;
  }

  public boolean hasAdditionalParameter(String name) {
    String paramName = new PropertyTokenizer(name).getName();
    return additionalParameters.containsKey(paramName);
  }

  public void setAdditionalParameter(String name, Object value) {
    metaParameters.setValue(name, value);
  }

  public Object getAdditionalParameter(String name) {
    return metaParameters.getValue(name);
  }
}
