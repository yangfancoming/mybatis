package org.apache.ibatis.reflection.model;


/**
 * Created by 64274 on 2019/7/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/11---9:47
 */
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
