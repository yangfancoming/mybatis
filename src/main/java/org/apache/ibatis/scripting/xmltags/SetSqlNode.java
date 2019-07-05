
package org.apache.ibatis.scripting.xmltags;

import java.util.Collections;
import java.util.List;

import org.apache.ibatis.session.Configuration;


public class SetSqlNode extends TrimSqlNode {

  private static final List<String> COMMA = Collections.singletonList(",");

  public SetSqlNode(Configuration configuration,SqlNode contents) {
    super(configuration, contents, "SET", COMMA, null, COMMA);
  }

}
