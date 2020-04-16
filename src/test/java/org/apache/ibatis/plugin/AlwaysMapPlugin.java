package org.apache.ibatis.plugin;

import java.util.Map;

/**
 * Created by Administrator on 2020/4/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---13:31
 */
@Intercepts({ @Signature(type = Map.class, method = "get", args = {Object.class})})
public class AlwaysMapPlugin implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) {
    return "Always";
  }
}
