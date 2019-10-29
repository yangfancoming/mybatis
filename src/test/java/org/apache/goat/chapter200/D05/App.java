package org.apache.goat.chapter200.D05;

import org.junit.Test;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---19:33
 */
public class App {

  @Test
  public void test(){
    Target target = new TargetImpl();
    //返回的是代理对象，实现了Target接口，
    //实际调用方法的时候，是调用TargetProxy的invoke()方法
    Target targetProxy = (Target) TargetProxy.wrap(target);
    targetProxy.execute(" HelloWord ");
  }
}
