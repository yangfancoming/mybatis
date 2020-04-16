package org.apache.ibatis.plugin;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/4/16.
 * @ Description: 就是将 @Intercepts 内容转换为 Map<Class<?>, Set<Method>>
 * @ date 2020/4/16---13:46
 * @see Plugin#signatureMap
 */
@Intercepts({
  @Signature(type = Map.class, method = "get", args = {Object.class}),
  @Signature(type = Map.class, method = "put", args = {Object.class,Object.class}),
  @Signature(type = List.class, method = "add", args = {Object.class}),
})
public class MorePlugin implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) {
    return "??";
  }
}
