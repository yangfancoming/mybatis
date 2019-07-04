
package org.apache.ibatis.submitted.parent_reference_3level;

import java.util.List;

public class Blog {

  private int id;
  private String title;
  private List<Post> posts;

  public Blog() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (this.title != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.title = title;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    if (this.posts != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.posts = posts;
  }
}
