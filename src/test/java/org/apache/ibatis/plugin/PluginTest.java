
package org.apache.ibatis.plugin;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PluginTest extends BaseTest {

  @Test
  void test() {
    Object plugin = alwaysMapPlugin.plugin(map);
    System.out.println(plugin);
    Map temp = (Map) plugin;
    assertEquals("Always", temp.get("Anything"));
  }

  @Test
  void mapPluginShouldInterceptGet() {
    map = (Map) alwaysMapPlugin.plugin(map);
    assertEquals("Always", map.get("Anything"));
  }

  @Test
  void shouldNotInterceptToString() {
    map = (Map) alwaysMapPlugin.plugin(map);
    assertNotEquals("Always", map.toString());
  }

}
