package pt.ulisboa.tecnico.master_process.databases.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Participant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Integer id;

    @Column(name = "first_name", length = 64, nullable = false)
    protected String firstName;

    @Column(name = "last_name", length = 64, nullable = false)
    protected String lastName;

    @Column(name = "email", unique = true, length = 128, nullable = false)
    protected String email;

    @Column(name = "gender", length = 6, nullable = false)
    protected Gender gender;

    @Column(name = "age", nullable = false)
    protected Integer age;

    public Participant(String fName, String lName, String emailAddress, Gender gender, int age) {
        this.firstName = fName;
        this.lastName = lName;
        this.email = emailAddress;
        this.gender = gender;
        this.age = age;
    }

    protected Participant() {}

    public Integer getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                '}';
    }
}
