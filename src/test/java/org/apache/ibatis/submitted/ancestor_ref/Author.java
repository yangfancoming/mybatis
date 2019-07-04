

package org.apache.ibatis.submitted.ancestor_ref;

public class Author {
  private Integer id;
  private String name;
  private Blog blog;
  private Reputation reputation;

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

  public Blog getBlog() {
    return blog;
  }

  public void setBlog(Blog blog) {
    this.blog = blog;
  }

  public Reputation getReputation() {
    return reputation;
  }

  public void setReputation(Reputation reputation) {
    this.reputation = reputation;
  }
}
