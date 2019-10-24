
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;


// 动态SQL
public class DynamicSqlSource implements SqlSource {

  private final Configuration configuration;
  //记录待解析的SqlNode的根节点，在构造函数中引入
  private final SqlNode rootSqlNode;

  public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
    this.configuration = configuration;
    this.rootSqlNode = rootSqlNode;
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    //创建DynamicContext对象，parameterObject为用户传入的实参
    DynamicContext context = new DynamicContext(configuration, parameterObject);
    //从根节点开始，解析根节点下的所有子节点得到的SQL片段追加到context中，最终通过context.getSql()得到完整的SQL语句，这是组合模式最大的好处
    rootSqlNode.apply(context);
    //创建SqlSourceBuilder //创建SqlSourceBuilder对象，解析参数属性，将SQL语句中的"#{}"解析成"?"占位符
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
    //解析statement节点，并返回sqlSource
    SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
    //创建BoundSql对象，并将DynamicContext.bindings中参数信息复制到其additionalParameters集合中保存
    BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
    context.getBindings().forEach(boundSql::setAdditionalParameter);
    return boundSql;
  }

}
