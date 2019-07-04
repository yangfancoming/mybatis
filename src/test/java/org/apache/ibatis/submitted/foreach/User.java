
package org.apache.ibatis.submitted.foreach;

import java.util.List;

public class User {

  private Integer id;
  private String name;
  private User bestFriend;
  private List<User> friendList;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getBestFriend() {
    return bestFriend;
  }

  public void setBestFriend(User bestFriend) {
    this.bestFriend = bestFriend;
  }

  public List<User> getFriendList() {
    return friendList;
  }

  public void setFriendList(List<User> friendList) {
    this.friendList = friendList;
  }
}
