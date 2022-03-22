package pt.ulisboa.tecnico.master_process.escapetheroom.service.participants;

import pt.ulisboa.tecnico.master_process.databases.entities.Gender;

import java.util.Objects;

/**
 * Represents a participant of a game.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class Participant {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private Gender gender;
    private int age;
    private int ID;

    public Participant(String fName, String lName, String mail, int age, String gender) {
        this.setFirstName(fName);
        this.setLastName(lName);
        this.setEmailAddress(mail);
        this.setAge(age);
        this.setGender(gender);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().equals("") || firstName.length() == 0) {
            throw new IllegalArgumentException("The First Name cannot be empty or Null");
        } else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().equals("") || lastName.length() == 0) {
            throw new IllegalArgumentException("The Last Name cannot be empty or Null");
        } else {
            this.lastName = lastName;
        }
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        if (emailAddress == null || emailAddress.trim().equals("") || emailAddress.length() == 0) {
            throw new IllegalArgumentException("E-Mail address cannot be empty or Null");
        } else {
            this.emailAddress = emailAddress;
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be above 0");
        } else {
            this.age = age;
        }
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(String genderString) {
        if (genderString == null) {
            throw new NullPointerException("Gender cannot be empty or Null");
        } else if (genderString.equalsIgnoreCase("MALE")) {
            this.gender = Gender.MALE;
        } else if (genderString.equalsIgnoreCase("FEMALE")) {
            this.gender = Gender.FEMALE;
        } else if (genderString.equalsIgnoreCase("NON-BINARY")) {
            this.gender = Gender.NON_BINARY;
        } else {
            throw new IllegalArgumentException("Invalid Gender");
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return age == that.age &&
                firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                emailAddress.equals(that.emailAddress) &&
                gender.equals(that.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, emailAddress, age, gender);
    }
}
