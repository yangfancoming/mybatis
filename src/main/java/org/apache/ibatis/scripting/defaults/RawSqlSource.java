
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
 * Static SqlSource. It is faster than {@link DynamicSqlSource} because mappings are calculated during startup.
 *  静态 sqlsource 它比dynamicsqlsource 快，因为映射是在启动期间计算的。
 *  常用的mybatis解析sql帮助类
 *
 *  RawSqlSource是SqlSource的另一个实现,逻辑与DynamicSqlSource类似，
 *  但是执行时机不同，处理的SQL语句类型也不同，
 *  在XMLScriptBuilder.parseDynamicTags()方法中，如果节点只包含"#{}"占位符，不包含动态SQL节点或未解析的"${}"占位符，
 *  则不是动态SQL语句，会创建StaticTextSqlNode对象。
 *  在XMLScriptBuilder.parseSciptNode()方法中会判断整个SQL节点是否为动态节点，
 *  如果不是动态SQL节点，则创建响应的RawSqlSource对象
 * @since 3.2.0
 */
public class RawSqlSource implements SqlSource {
  //内部封装的sqlSource对象，getBoundSql方法会委托给这个对象
  private final SqlSource sqlSource;

  public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
    //调用getSql方法，完成SQL语句的拼装和初步解析，与DynamicSqlSource中相同
    this(configuration, getSql(configuration, rootSqlNode), parameterType);
  }

  public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
    //创建sqlSourceBuilder  // 通过SqlSourceBuilder来创建sqlSource  //通过SqlSourceBuilder完成占位符的解析和替换操作
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> clazz = parameterType == null ? Object.class : parameterType;
    //解析sql，创建StaticSqlSource对象  //返回StaticSqlSource
    sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<>());
  }

  /**
   * 通过遍历所有的SqlNode，获取sql语句
   */
  private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
    DynamicContext context = new DynamicContext(configuration, null);
    //这里的rootSqlNode就是之前得到的MixedSqlNode，它会遍历内部的SqlNode,逐个调用sqlNode的apply方法。StaticTextSqlNode会直接context.appendSql方法
    //rootSqlNode为MixedSqlNode
    rootSqlNode.apply(context);
    return context.getSql();
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    //此处的sqlSource为RawSqlSource的内部属性
    return sqlSource.getBoundSql(parameterObject);
  }

}
