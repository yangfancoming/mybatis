
package org.apache.ibatis.submitted.parent_reference_3level;

import java.util.List;

public class Post {

  private int id;
  private String body;
  private Blog blog;
  private List<Comment> comments;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Blog getBlog() {
    return blog;
  }

  public void setBlog(Blog blog) {
    if (this.blog != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.blog = blog;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    if (this.body != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.body = body;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    if (this.comments != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.comments = comments;
  }
}
