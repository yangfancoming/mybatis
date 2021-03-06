
package org.apache.ibatis.domain.blog;

public class PostLiteId {

  private int id;

  public PostLiteId() {

  }

  public PostLiteId(int aId) {
    id = aId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final PostLiteId that = (PostLiteId) o;

    if (id != that.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
