package org.apache.ibatis.plugin;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/4/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---13:46
 */

@Intercepts({
  @Signature(type = Map.class, method = "get", args = {Object.class}),
  @Signature(type = List.class, method = "add", args = {Object.class}),
})
public class MorePlugin implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) {
    return "??";
  }
}
