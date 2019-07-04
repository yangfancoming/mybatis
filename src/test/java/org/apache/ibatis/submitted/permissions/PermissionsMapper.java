
package org.apache.ibatis.submitted.permissions;

import java.util.List;

public interface PermissionsMapper {

  List<Resource> getResources();

  List<Resource> getResource(String permission);

}
