
package org.apache.ibatis.parsing;


public interface TokenHandler {

  /**
   * 对字符串进行处理
   * @param content
   */
  String handleToken(String content);
}

