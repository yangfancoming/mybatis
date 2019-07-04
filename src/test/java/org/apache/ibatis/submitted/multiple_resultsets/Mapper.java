
package org.apache.ibatis.submitted.multiple_resultsets;

import java.util.List;

public interface Mapper {

  List<List<?>> getUsersAndGroups();

  List<List<?>> multiResultsWithUpdate();

}
