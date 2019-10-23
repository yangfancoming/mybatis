package org.apache.goat.model;

public class User {

  public Integer id;
  public String name;
	private String user;
	private String password;

	public User() {
	}

	public User(String user, String password) {
		super();
		this.user = user;
		this.password = password;
	}


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

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
