
package org.apache.ibatis.submitted.immutable_constructor;

import java.io.Serializable;

public class ImmutablePOJO implements Serializable {

  private static final long serialVersionUID = -7086198701202598455L;
  private final Integer immutableId;
  private final String immutableDescription;

  public ImmutablePOJO(Integer immutableId, String immutableDescription) {
    this.immutableId = immutableId;
    this.immutableDescription = immutableDescription;
  }

  public Integer getImmutableId() {
    return immutableId;
  }

  public String getImmutableDescription() {
    return immutableDescription;
  }

}
