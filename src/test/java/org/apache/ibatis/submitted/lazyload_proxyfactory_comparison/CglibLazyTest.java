
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

public class CglibLazyTest extends AbstractLazyTest {
  @Override
  protected String getConfiguration() {
    return "cglib";
  }
}
