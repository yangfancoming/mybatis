
package org.apache.ibatis.submitted.refcursor;

import java.util.Map;

import org.apache.ibatis.session.ResultHandler;

public interface OrdersMapper {
  void getOrder1(Map<String, Object> parameter);

  void getOrder2(Map<String, Object> parameter);

  void getOrder3(Map<String, Object> parameter, ResultHandler<Order> resultHandler);
}
