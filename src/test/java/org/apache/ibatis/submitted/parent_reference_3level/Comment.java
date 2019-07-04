
package org.apache.ibatis.submitted.parent_reference_3level;

public class Comment {

  private int id;
  private Post post;
  private String comment;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Post getPost() {
    return post;
  }

  public void setPost(Post post) {
    if (this.post != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.post = post;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    if (this.comment != null) {
      throw new RuntimeException("Setter called twice");
    }
    this.comment = comment;
  }
}
