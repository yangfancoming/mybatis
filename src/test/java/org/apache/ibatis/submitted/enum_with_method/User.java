
package org.apache.ibatis.submitted.enum_with_method;

public class User {

    private Integer id;
    private String name;
    private Currency cur;

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

    public Currency getCur() {
        return cur;
    }

    public void setCur(Currency cur) {
        this.cur = cur;
    }
}
