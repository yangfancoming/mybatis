package org.apache.goat.chapter200.D05;

/**
 * Created by 64274 on 2019/10/29.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/29---19:30
 */
public class TargetImpl implements Target {

  @Override
  public String execute(String name) {
    System.out.println("普通业务方法执行 ----- execute()  接收参数为："+ name);
    return name;
  }

  @Override
  public String test(String name) {
    System.out.println("普通业务方法执行 ----- test()  接收参数为："+ name);
    return name;
  }
}
