
package org.apache.ibatis.submitted.lazy_deserialize;

import java.io.Serializable;

/**
 * @since 2011-04-06T10:57:30+0200
 */
public class LazyObjectFoo implements Serializable {

  private static final long serialVersionUID = 1L;
  private Integer id;
  private LazyObjectBar lazyObjectBar;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LazyObjectBar getLazyObjectBar() {
    return this.lazyObjectBar;
  }

  public void setLazyObjectBar(final LazyObjectBar lazyObjectBar) {
    this.lazyObjectBar = lazyObjectBar;
  }

}
