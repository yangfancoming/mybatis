
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.session.Configuration;

import java.util.Collections;
import java.util.List;

/**
 很简单，就是固定了参数prefix=SET、suffix=null(会将suffixOverrides符合的条件置为空)、suffixOverrides=List[","]，其余调用父类方法即可，详见上文
*/
public class SetSqlNode extends TrimSqlNode {

  private static final List<String> COMMA = Collections.singletonList(",");

  public SetSqlNode(Configuration configuration,SqlNode contents) {
    super(configuration, contents, "SET", COMMA, null, COMMA);
  }

}
