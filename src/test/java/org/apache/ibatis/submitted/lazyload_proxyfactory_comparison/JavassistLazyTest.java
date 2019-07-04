
package org.apache.ibatis.submitted.lazyload_proxyfactory_comparison;

import org.junit.jupiter.api.Disabled;

@Disabled("See Issue 664: Javassist ProxyFactory does not handle interfaces with generics correctly.")
public class JavassistLazyTest
extends AbstractLazyTest {
  @Override
  protected String getConfiguration() {
    return "javassist";
  }
}
