
package org.apache.ibatis.scripting.defaults;

import java.util.HashMap;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

/**
 * Static SqlSource. It is faster than {@link DynamicSqlSource} because mappings are
 * calculated during startup.  静态SQL
 * 常用的mybatis解析sql帮助类
 * @since 3.2.0
 */
public class RawSqlSource implements SqlSource {
  //内部封装的sqlSource对象，getBoundSql方法会委托给这个对象
  private final SqlSource sqlSource;

  public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
    this(configuration, getSql(configuration, rootSqlNode), parameterType);
  }

  public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
    //创建sqlSourceBuilder  // 通过SqlSourceBuilder来创建sqlSource
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> clazz = parameterType == null ? Object.class : parameterType;
    //解析sql，创建StaticSqlSource对象
    sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
  }
  //获取sql语句
  private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
    DynamicContext context = new DynamicContext(configuration, null);
    //这里的rootSqlNode就是之前得到的MixedSqlNode，它会遍历内部的SqlNode,逐个调用sqlNode的apply方法。StaticTextSqlNode会直接context.appendSql方法
    rootSqlNode.apply(context);
    return context.getSql();
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    //此处的sqlSource为RawSqlSource的内部属性
    return sqlSource.getBoundSql(parameterObject);
  }

}
