
package org.apache.ibatis.scripting.xmltags;

import java.util.Map;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.session.Configuration;

/**
 * ForEachSqlNode，在动态SQL语句中，通常需要对一个集合进行迭代，Mybatis提供了<foreach>标签实现该功能。
 * 在使用<foreach>标签迭代集合时，不仅可以使用集合的元素和索引值，
 * 还可以在循环开始之前或结束之后添加指定的字符串，也允许在迭代过程中添加指定的分隔符
 * List参数的
 * <insert id="insertBetdetailsByBatch" parameterType="java.util.List">
 *    insert into betdetails(id,orderId,actorIndex,createTime,ballIndex,ballValue,betAmount,rate1,rate2,rate3,gameType,status,betResult,awardAmount,ballName) values
 *    <foreach collection="list" item="item" index="index" separator=",">
 *       (#{item.id},#{item.orderId},#{item.actorIndex},#{item.createTime},#{item.ballIndex},#{item.ballValue},#{item.betAmount},#{item.rate1},#{item.rate2},#{item.rate3},#{item.gameType},#{item.status},#{item.betResult},#{item.awardAmount},#{item.ballName})
 *    </foreach>
 * </insert>
 *
 * Map参数的
 * <select id="findAgentUserBalance" parameterType="java.util.Map" resultType="java.math.BigDecimal">
 *    SELECT SUM(banlance) FROM app_user
 *    <where>
 *       <if test="null != userIds">
 *          AND id IN
 *          <foreach collection="userIds" item="item" index="index" separator="," open="(" close=")">
 *             #{item}
 *          </foreach>
 *       </if>
 *    </where>
 * </select>
*/
public class ForEachSqlNode implements SqlNode {

  public static final String ITEM_PREFIX = "__frch_";
  // 用于判断循环的终止条件
  private final ExpressionEvaluator evaluator;
  // 迭代的集合表达式 <foreach collection="list" item="item" index="index" separator=","> 中 对应collection的值： list
  private final String collectionExpression;
  // 记录了该ForEachSqlNode节点的子节点
  private final SqlNode contents;
  // 在循环开始前要添加的字符串
  private final String open;
  // 在循环结束后要添加的字符串
  private final String close;
  // 循环过程中，每项之间的分隔符
  private final String separator;
  // 本次迭代的集合元素标识（相当于一个变量，用该变量来识别）
  private final String item;
  // 本次迭代的集合索引标识（相当于一个变量，用该变量来识别）
  private final String index;
  // 全局配置信息
  private final Configuration configuration;

  public ForEachSqlNode(Configuration configuration, SqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
    // 解析帮助类
    this.evaluator = new ExpressionEvaluator();
    // 集合别名
    this.collectionExpression = collectionExpression;
    this.contents = contents;
    this.open = open;
    this.close = close;
    this.separator = separator;
    this.index = index;
    this.item = item;
    this.configuration = configuration;
  }

  @Override
  public boolean apply(DynamicContext context) {
    // 获取用户传入的参数的上下文
    Map<String, Object> bindings = context.getBindings();
    // 将参数上下文作为root传入OGNL解析出类似#{item.id}的原值后将迭代的集合表达式还原成集合本身
    final Iterable<?> iterable = evaluator.evaluateIterable(collectionExpression, bindings);
    // 如果集合没有数据，直接返回true
    if (!iterable.iterator().hasNext()) return true;
    boolean first = true;
    // 在循环开始前处理要添加的字符SQL片段
    applyOpen(context);
    int i = 0;
    // 开始遍历集合，进入循环
    for (Object o : iterable) {
      // 将context缓存为另一个对象
      DynamicContext oldContext = context;
      // 如果是集合的第一项，或者分隔符为null
      if (first || separator == null) {
        // 以空前缀来构建context为PrefixedContext对象
        context = new PrefixedContext(context, "");
      } else {
        // 如果不是集合第一项，或者分隔符不为null,以分隔符为前缀来构建context为PrefixedContext对象
        context = new PrefixedContext(context, separator);
      }
      // 获取迭代计数器
      int uniqueNumber = context.getUniqueNumber();
      // Issue #709
      if (o instanceof Map.Entry) {
        // 如果集合是Map类型，将集合中key和value添加到DynamicContext.bindings集合中保存
        @SuppressWarnings("unchecked")
        Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
        applyIndex(context, mapEntry.getKey(), uniqueNumber);
        applyItem(context, mapEntry.getValue(), uniqueNumber);
      } else {
        // 将集合中的索引和元素添加到DynamicContext.bindings集合中保存
        applyIndex(context, i, uniqueNumber);
        applyItem(context, o, uniqueNumber);
      }
      // 调用子节点的apply()收集SQL语句，放入DynamicContext的代理类FilteredDynamicContext的StringBuilder中
      // 此处解析的是类如#{_frch_index_0},#{__frch_item_0}的标识
      contents.apply(new FilteredDynamicContext(configuration, context, index, item, uniqueNumber));
      if (first) {
        // 如果是第一项，将first更新为false（在applyOpen(context)中，prefixApplied已经被更新为true）
        first = !((PrefixedContext) context).isPrefixApplied();
      }
      // 还原context为原始DynamicContext，重新进入下一项循环
      context = oldContext;
      i++;
    }
    // 在循环结束后添加要处理的SQL片段
    applyClose(context);
    context.getBindings().remove(item);
    context.getBindings().remove(index);
    return true;
  }

  private void applyIndex(DynamicContext context, Object o, int i) {
    // 将集合的索引标识与放入的对象（map对象放入的是key,List对象放入的是真正的索引值）存入参考上下文中
    // 以上面配置的Map为例，假如传入的userIds为("小李飞刀",1),("霸天虎",2)，此处假设用户名不能重复，且执行到第一个
    // 此处存入的是("index","小李飞刀")，如果执行到第二个的时候，此处存入的是("index","霸天虎")
    if (index != null) {
      context.bind(index, o);
      //将集合的索引标识与计数器的连接绑定，与放入的对象存入参考上下文中
      //此处存入的是("_frch_index_0","小李飞刀")，执行到第二个的时候，此处存入的是("_frch_index_1","霸天虎")
      context.bind(itemizeItem(index, i), o);
    }
  }

  private void applyItem(DynamicContext context, Object o, int i) {
    if (item != null) {
      // 将集合的内容标识与放入的对象（map对象放入的为value，List对象放入的为列表中的对象元素）存入参考上下文中
      // 对应索引的内容，此处存入的是("item",1)，执行到第二个的时候，此处存入的是("item",2)
      context.bind(item, o);
      // 将集合的内容标识与计数器的连接绑定，与放入的对象存入参考上下文中,为子节点的进一步解析（真正替换每一次迭代项为集合实际的值）做准备
      // 此处存入的是("__frch_item_0",1)，执行到第二个的时候，此处存入的是("__frch_item_1",2)
      context.bind(itemizeItem(item, i), o);
    }
  }

  // 将开始循环前的SQL片段添加到DynamicContext的StringBuilder中
  private void applyOpen(DynamicContext context) {
    if (open != null)  context.appendSql(open);
  }

  // 将结束循环后的SQL片段添加到DynamicContext的StringBuilder中
  private void applyClose(DynamicContext context) {
    if (close != null) context.appendSql(close);
  }

  //返回的值类似为__frch_item_0（假设item="item",i=0）
  private static String itemizeItem(String item, int i) {
    return ITEM_PREFIX + item + "_" + i;
  }

  // FilteredDynamicContext为DynamicContext的代理类，负责处理#{}占位符
  private static class FilteredDynamicContext extends DynamicContext {
    // 委托的DynamicContext
    private final DynamicContext delegate;
    // 对应集合项在集合中的索引位置
    private final int index;
    // 对应集合项的index（索引标识）
    private final String itemIndex;
    // 对应集合项的item（元素标识）
    private final String item;

    public FilteredDynamicContext(Configuration configuration,DynamicContext delegate, String itemIndex, String item, int i) {
      super(configuration, null);
      this.delegate = delegate;
      this.index = i;
      this.itemIndex = itemIndex;
      this.item = item;
    }

    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }

    @Override
    public String getSql() {
      return delegate.getSql();
    }

    @Override
    public void appendSql(String sql) {
      // 解析类如("_frch_index_0","小李飞刀")，("__frch_item_0",1)的映射，给_frch_index_0，__frch_item_0变为OGNL可以识别的
      // #{_frch_index_0},#{__frch_item_0}
      GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
        String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
        if (itemIndex != null && newContent.equals(content)) {
          newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
        }
        return "#{" + newContent + "}";
      });
      // 通过OGNL来进行解析成对应映射的值
      delegate.appendSql(parser.parse(sql));
    }

    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }
  }


  private class PrefixedContext extends DynamicContext {
    // 对DynamicContext的委托
    private final DynamicContext delegate;
    // 指定的前缀
    private final String prefix;
    // 是否已经处理过前缀
    private boolean prefixApplied;

    public PrefixedContext(DynamicContext delegate, String prefix) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefix = prefix;
      this.prefixApplied = false;
    }

    public boolean isPrefixApplied() {
      return prefixApplied;
    }

    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }
    // 最主要的地方就是它重写了appendSql()方法，其他地方都是通过对delegate的委托，来实现一样的功能
    @Override
    public void appendSql(String sql) {
      // 如果没有处理过前缀且要添加的SQL语句不为空
      if (!prefixApplied && sql != null && sql.trim().length() > 0) {
        // 先把指定的前缀SQL片段添加到delegate中
        delegate.appendSql(prefix);
        // 修改为前缀已处理
        prefixApplied = true;
      }
      // 再把处理完的SQL语句片段添加到StringBuilder中
      delegate.appendSql(sql);
    }

    @Override
    public String getSql() {
      return delegate.getSql();
    }

    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }
  }

}
