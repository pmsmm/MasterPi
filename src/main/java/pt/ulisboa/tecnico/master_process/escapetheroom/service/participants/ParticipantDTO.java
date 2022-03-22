package pt.ulisboa.tecnico.master_process.escapetheroom.service.participants;

/**
 * Data transfer object that is used by the web client to send to the server.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class ParticipantDTO {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private int age;
    private String gender;

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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
