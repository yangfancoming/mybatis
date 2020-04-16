package org.apache.ibatis.plugin;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2020/4/16.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/16---13:29
 */
public class PluginSimpleTest extends BaseTest {

  Map<Class<?>, Set<Method>> signatureMap = Plugin.getSignatureMap(morePlugin);

  @Test
  public void testGetSignatureMap1(){
    Set<Method> methods = signatureMap.get(Map.class);
    Assert.assertEquals(2,methods.size());
  }

  @Test
  public void testGetSignatureMap2(){
    Set<Method> methods2 = signatureMap.get(List.class);
    Assert.assertEquals(1,methods2.size());
  }

  @Test
  public void testGetAllInterfaces(){

    Class<?>[] allInterfaces = Plugin.getAllInterfaces(Map.class, signatureMap);
    System.out.println(allInterfaces);
  }


}
