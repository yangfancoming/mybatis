
package org.apache.ibatis.session;

/**
 * @author Clinton Begin
 */
public interface ResultContext<T> {

  T getResultObject();

  int getResultCount();

  boolean isStopped();

  void stop();

}
