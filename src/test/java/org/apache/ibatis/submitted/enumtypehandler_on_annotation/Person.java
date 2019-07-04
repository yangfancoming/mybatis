
package org.apache.ibatis.submitted.enumtypehandler_on_annotation;

/**
 * @since #444
 * @author Kazuki Shimizu
 */
public class Person {

    enum PersonType {
        PERSON,
        EMPLOYEE
    }

    private Integer id;
    private String firstName;
    private String lastName;
    private PersonType personType;

    public Person() {
    }

    public Person(Integer id, String firstName, String lastName, PersonType personType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personType = personType;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public PersonType getPersonType() {
        return personType;
    }
    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

}
