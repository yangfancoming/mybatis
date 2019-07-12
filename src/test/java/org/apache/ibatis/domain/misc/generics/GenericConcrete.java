
package org.apache.ibatis.domain.misc.generics;

public class GenericConcrete extends GenericSubclass implements GenericInterface<Long> {

  private Long id;

  // 抽象类 实现
  @Override
  public Long getId() {
    return id;
  }

  // 接口 实现
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  public void setId(Integer id) {
    this.id = (long) id;
  }

  public void setId(String id) {
    this.id = Long.valueOf(id);
  }
}
