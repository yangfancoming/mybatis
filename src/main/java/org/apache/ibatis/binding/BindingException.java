
package org.apache.ibatis.binding;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * 绑定例外，当Map中查不到对应的key时，抛此异常
 * 当重复添加映射时，也抛此异常
 * 当绑定Mapper中某个方法出错，也抛此异常
 */
public class BindingException extends PersistenceException {

  private static final long serialVersionUID = 4300802238789381562L;

  public BindingException() {
    super();
  }

  public BindingException(String message) {
    super(message);
  }

  public BindingException(String message, Throwable cause) {
    super(message, cause);
  }

  public BindingException(Throwable cause) {
    super(cause);
  }
}
