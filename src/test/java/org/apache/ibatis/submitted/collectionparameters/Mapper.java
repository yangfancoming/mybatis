
package org.apache.ibatis.submitted.collectionparameters;

import java.util.List;
import java.util.Set;

public interface Mapper {

  List<User> getUsersFromList(List<Integer> id);

  List<User> getUsersFromArray(Integer[] id);

  List<User> getUsersFromCollection(Set<Integer> id);

}
