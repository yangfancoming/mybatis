
package org.apache.ibatis.plugin;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PluginTest {

  Map map = new HashMap();

  @Test
  void test() {
    AlwaysMapPlugin alwaysMapPlugin = new AlwaysMapPlugin();
    Object plugin = alwaysMapPlugin.plugin(map);
    System.out.println(plugin);

    Map temp = (Map) plugin;
    System.out.println(temp.get("Anything"));
  }

  @Test
  void mapPluginShouldInterceptGet() {
    map = (Map) new AlwaysMapPlugin().plugin(map);
    assertEquals("Always", map.get("Anything"));
  }

  @Test
  void shouldNotInterceptToString() {
    map = (Map) new AlwaysMapPlugin().plugin(map);
    assertNotEquals("Always", map.toString());
  }

  @Intercepts({ @Signature(type = Map.class, method = "get", args = {Object.class})})
  public static class AlwaysMapPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) {
      return "Always";
    }

  }

}
