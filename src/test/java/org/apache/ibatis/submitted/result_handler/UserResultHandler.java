
package org.apache.ibatis.submitted.result_handler;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

/**
  自定义 的 结果集处理器  需要实现 ResultHandler 接口
*/
public class UserResultHandler implements ResultHandler {
  private List<User> users;

  public UserResultHandler() {
    super();
    users = new ArrayList<>();
  }

  @Override
  public void handleResult(ResultContext context) {
    User user = (User) context.getResultObject();
    users.add(user);
    users.add(user);
  }

  public List<User> getUsers() {
    return users;
  }
}
