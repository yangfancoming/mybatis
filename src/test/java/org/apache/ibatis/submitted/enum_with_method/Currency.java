
package org.apache.ibatis.submitted.enum_with_method;

import java.math.BigDecimal;

public enum Currency {

    Dollar {
        @Override
        public BigDecimal getExchange() {
            return null;
        }
    },

    RMB {
        @Override
        public BigDecimal getExchange() {
            return null;
        }
    };

    public abstract BigDecimal getExchange();
}
