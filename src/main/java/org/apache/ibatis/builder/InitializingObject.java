
package org.apache.ibatis.builder;

/**
 * Interface that indicate to provide a initialization method.
 * @since 3.4.2
 */
public interface InitializingObject {

  /**
   * Initialize a instance.
   * This method will be invoked after it has set all properties.
   * @throws Exception in the event of misconfiguration (such as failure to set an essential property) or if initialization fails
   */
  void initialize() throws Exception;

}
