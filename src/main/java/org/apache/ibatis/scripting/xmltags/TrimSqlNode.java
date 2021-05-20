
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.session.Configuration;

import java.util.*;

/**
 * TrimSqlNode会根据子节点的解析结果，添加或删除响应的前缀或后缀，比如有这么一段配置
*/
public class TrimSqlNode implements SqlNode {
  //该<trim>节点的子节点
  private final SqlNode contents;
  //记录了前缀字符串（为<trim>节点包裹的SQL语句添加的前缀）
  private final String prefix;
  //记录了后缀字符串（为<trim>节点包裹的SQL语句添加的后缀）
  private final String suffix;
  //如果<trim>节点包裹的SQL语句是空语句，删除指定的前缀，如where
  private final List<String> prefixesToOverride;
  //如果<trim>节点包裹的SQL语句是空语句，删除指定的后缀，如逗号
  private final List<String> suffixesToOverride;
  private final Configuration configuration;

  public TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, String prefixesToOverride, String suffix, String suffixesToOverride) {
    this(configuration, contents, prefix, parseOverrides(prefixesToOverride), suffix, parseOverrides(suffixesToOverride));
  }
  //这里 contents 一般为MixedSqlNode，内部包含多个SqlNode
  protected TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, List<String> prefixesToOverride, String suffix, List<String> suffixesToOverride) {
    this.contents = contents;
    this.prefix = prefix;
    this.prefixesToOverride = prefixesToOverride;
    this.suffix = suffix;
    this.suffixesToOverride = suffixesToOverride;
    this.configuration = configuration;
  }

  @Override
  public boolean apply(DynamicContext context) {
    //创建FilteredDynamicContext对象，FilteredDynamicContext是TrimSqlNode的内部类，继承于DynamicContext
    FilteredDynamicContext filteredDynamicContext = new FilteredDynamicContext(context);
    //调用子节点的apply()方法进行解析，注意收集SQL语句的是filteredDynamicContext
    boolean result = contents.apply(filteredDynamicContext);
    //处理前缀和后缀
    filteredDynamicContext.applyAll();
    return result;
  }

  private static List<String> parseOverrides(String overrides) {
    if (overrides != null) {
      final StringTokenizer parser = new StringTokenizer(overrides, "|", false);
      final List<String> list = new ArrayList<>(parser.countTokens());
      while (parser.hasMoreTokens()) {
        list.add(parser.nextToken().toUpperCase(Locale.ENGLISH));
      }
      return list;
    }
    return Collections.emptyList();
  }

  private class FilteredDynamicContext extends DynamicContext {
    //底层封装委托的DynamicContext对象
    private DynamicContext delegate;
    //是否已经处理过前缀
    private boolean prefixApplied;
    //是否已经处理过后缀
    private boolean suffixApplied;
    //用于记录子节点解析后的结果
    private StringBuilder sqlBuffer;

    public FilteredDynamicContext(DynamicContext delegate) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefixApplied = false;
      this.suffixApplied = false;
      this.sqlBuffer = new StringBuilder();
    }

    public void applyAll() {
      //获取子节点解析后的结果，并全部转化为大写
      sqlBuffer = new StringBuilder(sqlBuffer.toString().trim());
      String trimmedUppercaseSql = sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
      if (trimmedUppercaseSql.length() > 0) {
        //处理前缀
        applyPrefix(sqlBuffer, trimmedUppercaseSql);
        //处理后缀
        applySuffix(sqlBuffer, trimmedUppercaseSql);
      }
      //将解析后的结果SQL片段添加到DynamicContext的StringBuilder中
      delegate.appendSql(sqlBuffer.toString());
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
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

    @Override
    public void appendSql(String sql) {
      sqlBuffer.append(sql);
    }

    @Override
    public String getSql() {
      return delegate.getSql();
    }

    private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
      //如果还没有处理过前缀
      if (!prefixApplied) {
        //更新为已处理
        prefixApplied = true;
        //如果需要删除的前缀列表不为null
        if (prefixesToOverride != null) {
          //遍历该前缀列表
          for (String toRemove : prefixesToOverride) {
            //如果<trim>子节点收集上来的SQL语句以该前缀开头
            if (trimmedUppercaseSql.startsWith(toRemove)) {
              //从<trim>子节点收集上来的StringBuilder中删除该前端
              sql.delete(0, toRemove.trim().length());
              break;
            }
          }
        }
        //如果有前缀字符串（比如说"("）,将前缀字符串插入StringBuilder最前端
        if (prefix != null) {
          sql.insert(0, " ");
          sql.insert(0, prefix);
        }
      }
    }

    private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
      //如果还没有处理过后缀
      if (!suffixApplied) {
        //更新为已处理后缀
        suffixApplied = true;
        //如果需要处理的后缀列表不为null
        if (suffixesToOverride != null) {
          //遍历该后缀列表
          for (String toRemove : suffixesToOverride) {
            //如果从<trim>子节点收集上来的SQL语句以该后缀结尾
            if (trimmedUppercaseSql.endsWith(toRemove) || trimmedUppercaseSql.endsWith(toRemove.trim())) {
              //获取该后缀的起始位置
              int start = sql.length() - toRemove.trim().length();
              //获取该后缀的终止位置
              int end = sql.length();
              //从<trim>子节点收集上来的StringBuilder中删除该后端
              sql.delete(start, end);
              break;
            }
          }
        }
        //如果有后缀字符串（比如说")"）,将前缀字符串拼接上StringBuilder最后端
        if (suffix != null) {
          sql.append(" ");
          sql.append(suffix);
        }
      }
    }

  }

}
