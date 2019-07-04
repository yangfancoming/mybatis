

package org.apache.ibatis.submitted.enum_interface_type_handler;

public enum Color implements HasValue {
  WHITE {
    public int getValue() {
      return 1;
    }
  },
  RED {
    public int getValue() {
      return 2;
    }
  },
  BLUE {
    public int getValue() {
      return 3;
    }
  }
}
