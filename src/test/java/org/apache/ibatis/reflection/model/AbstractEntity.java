package org.apache.ibatis.reflection.model;


/**
 * Created by 64274 on 2019/7/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/11---9:47
 */
// doit 这里 到底 有没有必要 implements Entity<Long>  因为 AbstractEntity 已经实现了该接口
public abstract class AbstractEntity implements Entity<Long> {

  private Long id;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }
}
