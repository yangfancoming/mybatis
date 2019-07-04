
package org.apache.ibatis.submitted.multidb;

public interface MultiDbMapper {
  String select1(int id);

  String select2(int id);

  String select3(int id);

  String select4(int id);

  void insert(User user);

  void insert2(User user);
}
