
package org.apache.ibatis.submitted.sqlprovider;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@BaseMapper.Meta(tableName = "users")
public interface Mapper extends BaseMapper<User> {
  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersQuery")
  List<User> getUsers(List<Integer> allFilterIds);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUserQuery")
  User getUser(Integer userId);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetAllUsersQuery")
  List<User> getAllUsers();

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByCriteriaQuery")
  List<User> getUsersByCriteria(User criteria);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByCriteriaMapQuery")
  List<User> getUsersByCriteriaMap(Map<String, Object> criteria);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByNameQuery")
  List<User> getUsersByName(String name, String orderByColumn);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByNameUsingMap")
  List<User> getUsersByNameUsingMap(String name, String orderByColumn);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByNameWithParamNameAndOrderByQuery")
  List<User> getUsersByNameWithParamNameAndOrderBy(@Param("name") String name, @Param("orderByColumn") String orderByColumn);

  @SelectProvider(type = OurSqlBuilder.class, method = "buildGetUsersByNameWithParamNameQuery")
  List<User> getUsersByNameWithParamName(@Param("name") String name);

  @InsertProvider(type = OurSqlBuilder.class, method = "buildInsert")
  void insert(User user);

  @UpdateProvider(type= OurSqlBuilder.class, method= "buildUpdate")
  void update(User user);

  @DeleteProvider(type= OurSqlBuilder.class, method= "buildDelete")
  void delete(Integer id);

}
