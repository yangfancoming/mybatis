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
    System.out.println("TargetImpl的业务方法执行 ----- execute() "+ name);
    return name;
  }
}
