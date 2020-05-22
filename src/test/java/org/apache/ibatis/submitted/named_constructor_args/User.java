
package org.apache.ibatis.submitted.named_constructor_args;

import org.apache.ibatis.annotations.Param;

public class User {

  private Integer id;
  private String name;
  private Long team;

  public User(@Param("id") String id) {
    super();
    System.out.println("1个参数构造方法执行");
    this.id = Integer.valueOf(id);
  }

  public User(Integer userId, @Param("name") String userName) {
    super();
    System.out.println("2个参数构造方法执行");
    this.id = userId;
    this.name = userName;
  }

  public User(@Param("id") int id, @Param("name") String name, @Param("team") String team) {
    super();
    System.out.println("3个参数构造方法执行 int");
    // NOP constructor to make sure MyBatis performs strict type matching.
  }

  public User(@Param("id") Integer id, @Param("name") String name, @Param("team") String team) {
    super();
    System.out.println("3个参数构造方法执行 Integer");
    this.id = id;
    this.name = name;
    this.team = Long.valueOf(team);
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Long getTeam() {
    return team;
  }
}
