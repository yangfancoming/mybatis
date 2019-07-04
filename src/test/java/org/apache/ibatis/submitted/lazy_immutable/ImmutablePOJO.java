
package org.apache.ibatis.submitted.lazy_immutable;

import java.io.Serializable;

public class ImmutablePOJO implements Serializable {

    private static final long serialVersionUID = -7086198701202598455L;
    private final Integer id;
    private final String description;

    public ImmutablePOJO(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

}
