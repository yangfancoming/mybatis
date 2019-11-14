
package org.apache.goat.chapter500.GenericTokenParser;


public interface TokenHandler {

  /**
   * 对字符串进行处理
   * @param content
   */
  String handleToken(String content);
}

