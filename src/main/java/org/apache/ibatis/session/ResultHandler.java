
package org.apache.ibatis.session;

/**
 * @author Clinton Begin
 */
public interface ResultHandler<T> {

  void handleResult(ResultContext<? extends T> resultContext);

}
