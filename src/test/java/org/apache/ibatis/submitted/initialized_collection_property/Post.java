
package org.apache.ibatis.submitted.initialized_collection_property;

public class Post {

  private int id;
  private String content;

  public Post() {
  }

  public Post(int id, String content) {
    this.id = id;
    this.content = content;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
