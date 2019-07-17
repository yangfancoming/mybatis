package org.apache.ibatis.reflection.test;

/**
 * Created by 64274 on 2019/7/6.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/7/6---10:13
 */
public class Author {

  private Integer id;
  private String name;
  private Integer age;
  /** 一个作者对应多篇文章 */
  private Article[] articles;


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

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Article[] getArticles() {
    return articles;
  }

  public void setArticles(Article[] articles) {
    this.articles = articles;
  }
}
