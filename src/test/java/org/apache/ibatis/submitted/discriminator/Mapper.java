
package org.apache.ibatis.submitted.discriminator;

import java.util.List;

public interface Mapper {

  List<Vehicle> selectVehicles();
  List<Owner> selectOwnersWithAVehicle();

}
