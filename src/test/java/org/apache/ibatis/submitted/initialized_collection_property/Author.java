
package org.apache.ibatis.submitted.initialized_collection_property;

import java.util.ArrayList;
import java.util.List;

public class Author {

  private long id;
  private List<Post> posts = new ArrayList<>();
  private String name;

  public Author() {
    posts.add(new Post(4, "there is a previous post!"));
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
