
package org.apache.ibatis.submitted.custom_collection_handling;

public class Person {

    private Integer id;
    private String name;
    private CustomCollection<Contact> contacts;

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

    public CustomCollection<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(CustomCollection<Contact> contacts) {
        this.contacts = contacts;
    }

}
