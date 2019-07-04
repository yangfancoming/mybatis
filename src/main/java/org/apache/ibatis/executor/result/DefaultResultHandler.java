
package org.apache.ibatis.executor.result;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author Clinton Begin
 */
public class DefaultResultHandler implements ResultHandler<Object> {

  private final List<Object> list;

  public DefaultResultHandler() {
    list = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  public DefaultResultHandler(ObjectFactory objectFactory) {
    list = objectFactory.create(List.class);
  }

  @Override
  public void handleResult(ResultContext<?> context) {
    list.add(context.getResultObject());
  }

  public List<Object> getResultList() {
    return list;
  }

}
