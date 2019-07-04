
package org.apache.ibatis.submitted.multiple_discriminator;

public interface PersonMapper {

  Person get(Long id);

  Person get2(Long id);

  Person getLoop();
}
