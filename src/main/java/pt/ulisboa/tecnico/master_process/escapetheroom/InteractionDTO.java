package pt.ulisboa.tecnico.master_process.escapetheroom;

/**
 * Data transfer object that is used to send information from the web client to the server
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class InteractionDTO {

    public String interactionPiAddress;
    public String interactionName;
    public Integer phase;

    public InteractionDTO() {

    }

    public InteractionDTO(String interactionPiAddress, String interactionName, int phase) {
        this.setInteractionPiAddress(interactionPiAddress);
        this.setInteractionName(interactionName);
        this.setPhase(phase);
    }

    public String getInteractionPiAddress() {
        return interactionPiAddress;
    }

    public void setInteractionPiAddress(String interactionPiAddress) {
        this.interactionPiAddress = interactionPiAddress;
    }

    public String getInteractionName() {
        return interactionName;
    }

    public void setInteractionName(String interactionName) {
        this.interactionName = interactionName;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }
}
