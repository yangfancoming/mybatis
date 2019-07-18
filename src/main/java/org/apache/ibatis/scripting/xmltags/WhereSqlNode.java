
package org.apache.ibatis.scripting.xmltags;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.Configuration;

/**
 很简单，就是固定了参数prefix=WHERE和prefixOverrides=List["AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t"]，其余调用父类方法即可
*/
public class WhereSqlNode extends TrimSqlNode {

  private static List<String> prefixList = Arrays.asList("AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

  public WhereSqlNode(Configuration configuration, SqlNode contents) {
    super(configuration, contents, "WHERE", prefixList, null, null);
  }

}
