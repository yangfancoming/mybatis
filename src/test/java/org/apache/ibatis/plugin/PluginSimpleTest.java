package org.apache.ibatis.plugin;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2020/4/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---13:29
 */
public class PluginSimpleTest extends BaseTest {

  @Test
  public void testGetSignatureMap(){
    Map<Class<?>, Set<Method>> signatureMap = Plugin.getSignatureMap(morePlugin);
    System.out.println(signatureMap);
  }
}
