
package org.apache.ibatis.scripting.xmltags;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.ibatis.builder.BuilderException;

/**
 * 在 MyBatis 中 ， 使用 OgnlCache 对原生的 OGNL 进行了封装 。 OGNL 表达式的解析过程是
 * 比较耗时的， 为了提高效率，OgnlCache 中使用 expressionCache 属性 ConcurrentHashMap  对解析后的 OGNL 表达式进行缓存
 * Caches OGNL parsed expressions.
 * @see <a href='http://code.google.com/p/mybatis/issues/detail?id=342'>Issue 342</a>
 */
public final class OgnlCache {

  private static final OgnlMemberAccess MEMBER_ACCESS = new OgnlMemberAccess();
  private static final OgnlClassResolver CLASS_RESOLVER = new OgnlClassResolver();
  private static final Map<String, Object> expressionCache = new ConcurrentHashMap<>();

  private OgnlCache() {
    // Prevent Instantiation of Static Class
  }

  public static Object getValue(String expression, Object root) {
    try {
      //创建OgnlContext对象
      Map context = Ognl.createDefaultContext(root, MEMBER_ACCESS, CLASS_RESOLVER, null);
      // 使用 OGNL 执行 expression 表达式
      return Ognl.getValue(parseExpression(expression), context, root);
    } catch (OgnlException e) {
      throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
    }
  }

  private static Object parseExpression(String expression) throws OgnlException {
    // 查找缓存
    Object node = expressionCache.get(expression);
    if (node == null) {
      // 解析表达式
      node = Ognl.parseExpression(expression);
      // 将表达式的解析结采添加到缓存中
      expressionCache.put(expression, node);
    }
    return node;
  }

}
