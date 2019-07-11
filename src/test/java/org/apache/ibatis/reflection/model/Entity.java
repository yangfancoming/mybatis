package org.apache.ibatis.reflection.model;

/**
 * Created by 64274 on 2019/7/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/11---9:48
 */
public interface Entity<T> {

  T getId();

  void setId(T id);
}
