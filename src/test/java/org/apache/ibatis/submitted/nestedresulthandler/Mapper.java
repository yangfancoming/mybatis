
package org.apache.ibatis.submitted.nestedresulthandler;

import java.util.List;

public interface Mapper {
  List<Person> getPersons();

  List<Person> getPersonsWithItemsOrdered();

  List<PersonItemPair> getPersonItemPairs();
}
