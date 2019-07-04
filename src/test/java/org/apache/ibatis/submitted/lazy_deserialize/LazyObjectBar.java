
package org.apache.ibatis.submitted.lazy_deserialize;

import java.io.Serializable;

/**
 * @since 2011-04-06T10:57:41+0200
 * @author Franta Mejta
 */
public class LazyObjectBar implements Serializable {

  private static final long serialVersionUID = 1L;
  private Integer id;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

}
