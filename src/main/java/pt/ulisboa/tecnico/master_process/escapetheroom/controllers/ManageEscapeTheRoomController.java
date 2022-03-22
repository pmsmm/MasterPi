package pt.ulisboa.tecnico.master_process.escapetheroom.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.master_process.escapetheroom.InteractionDTO;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.GameState;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.ParticipantDTO;
import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;
import pt.ulisboa.tecnico.master_process.interactions.CryptoServoInteraction;
import pt.ulisboa.tecnico.master_process.interactions.MorseCodeInteraction;
import pt.ulisboa.tecnico.rmi.games.Games;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

@Controller
@RequestMapping("/escapetheroom/instances")
public class ManageEscapeTheRoomController {

    @GetMapping
    public String allEscapeTheRoomInstances(Model model) {
        model.addAttribute("EscapeTheRoomInstances", EscapeTheRoomContainer.getInstance().getAllEscapeTheRooms().values());
        return "Escape The Room/Instances/EscapeTheRoomInstances";
    }

    @GetMapping(value = {"/{id}"})
    public String getEscapeTheRoomManagementPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("EscapeTheRoom", instance);
            model.addAttribute("EscapeTheRoomRunning", instance.getGameState() == GameState.RUNNING);
            model.addAttribute("EscapeTheRoomPaused", instance.getGameState() == GameState.PAUSED);
            return "Escape The Room/Manage Escape The Room/ManageEscapeTheRoom";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/clues"})
    public String getEscapeTheRoomCluesPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("Clues", instance.getAcquiredClues());
            return "Escape The Room/Clues/Clues";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/group-name"})
    public String getEscapeTheRoomGroupNamePage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);

            model.addAttribute("id", instance.getCreationDate());
            if (instance.getGroupName() == null) {
                model.addAttribute("groupName", "The Group Does Not Have a Name");
            } else {
                model.addAttribute("groupName", instance.getGroupName());
            }

            return "Escape The Room/Manage Escape The Room/Group Name/ManageGroupName";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/group-name/submit"})
    public String setEscapeTheRoomGroupName(@PathVariable(value = "id") String id,
                                            @RequestParam("value") Optional<String> groupName,
                                            Model model) {
        try {
            validateEscapeTheRoomID(id);

            if (!groupName.isPresent()) {
                throw new IllegalArgumentException("Please insert a Group Name");
            }

            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).setGroupName(groupName.get()));

            return "Feedback Pages/Success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/participants"})
    public String getEscapeTheRoomParticipants(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("Participants", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).getAllParticipants());
            model.addAttribute("EscapeTheRoomID", id);
            return "Escape The Room/Manage Escape The Room/Participants/EscapeTheRoomParticipants";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/participants/add"})
    public String getEscapeTheRoomAddParticipantPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("Participant", new ParticipantDTO());
            model.addAttribute("EscapeTheRoomID", id);
            return "Escape The Room/Manage Escape The Room/Participants/Add/AddParticipantPage";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/participants/add/submit"})
    public String addEscapeTheRoomParticipant(@PathVariable(value = "id") String id,
                                              @ModelAttribute ParticipantDTO participantDTO,
                                              Model model) {
        try {
            validateEscapeTheRoomID(id);
            Participant participant = new Participant(participantDTO.getFirstName(), participantDTO.getLastName(), participantDTO.getEmailAddress(),
                    participantDTO.getAge(), participantDTO.getGender());
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).addParticipant(participant));
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/participants/remove/{participantID}"})
    public String removeEscapeTheRoomParticipant(@PathVariable(value = "id") String id,
                                                 @PathVariable(value = "participantID") int participantID,
                                                 Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("SuccessMessage", instance.removeParticipant(participantID));
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/participants/remove/all"})
    public String removeAllEscapeTheRoomParticipants(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("SuccessMessage", instance.removeAllParticipants());
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/interactions"})
    public String getEscapeTheRoomInteractionsPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("EscapeTheRoomID", id);
            model.addAttribute("InteractionPies", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).getSelectedInteractions());
            return "Escape The Room/Manage Escape The Room/Interactions/EscapeTheRoomInteractions";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/interactions/add"})
    public String addInteractionToEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("EscapeTheRoomID", id);
            model.addAttribute("Interactions", InteractionPiesContainer.getInstance().getAllAvailableInteractions(id));
            model.addAttribute("InteractionDTO", new InteractionDTO());
            return "Escape The Room/Manage Escape The Room/Interactions/Add/AddInteractionToEscapeTheRoom";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.addAttribute("StackTrace", sw.toString());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"{id}/interactions/add/submit"})
    public String submitInteractionToEscapeTheRoom(@PathVariable(value = "id") String id,
                                                   @ModelAttribute InteractionDTO interactionDTO, Model model) {
        try {
            validateEscapeTheRoomID(id);
            if (!InteractionPiesContainer.getInteractionPiesHashMap().containsKey(interactionDTO.interactionPiAddress)) {
                throw new IllegalArgumentException("The Requested Interaction Pi Does Not Exist");
            }
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).
                    addInteraction(interactionDTO.interactionPiAddress, interactionDTO.interactionName, interactionDTO.phase));
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"{id}/interactions/remove/{InteractionPiAddress}/{InteractionName}"})
    public String removeInteractionFromEscapeTheRoom(@PathVariable(value = "id") String id,
                                                     @PathVariable(value = "InteractionPiAddress") String address,
                                                     @PathVariable(value = "InteractionName") String interactionName,
                                                     Model model) {
        try {
            validateEscapeTheRoomID(id);
            if (!InteractionPiesContainer.getInteractionPiesHashMap().containsKey(address)) {
                throw new IllegalArgumentException("The Requested Interaction Pi Does Not Exist");
            }
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).removeInteraction(address, interactionName));
            return "Feedback Pages/Success";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("ErrorMessage", exception.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"{id}/interactions/remove/all"})
    public String removeAllInteractionsFromEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).removeAllInteractions(null));
            return "Feedback Pages/Success";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/start"})
    public String startEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("SuccessMessage", instance.startGame());
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.addAttribute("StackTrace", sw.toString());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/status"})
    public String statusEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("GroupName", instance.getGroupName());
            model.addAttribute("Interactions", instance.getAllInteractions());
            model.addAttribute("TimeLeft", LocalTime.MIN.plusSeconds(
                    (instance.getEscapeTheRoomDurationInSeconds() - instance.getELAPSED_TIME())
            ).format(DateTimeFormatter.ofPattern("H:mm:ss")));
            model.addAttribute("CurrentScore", instance.getCurrentScore());
            model.addAttribute("PercentageOfConclusion", instance.getPercentageOfConclusion());
            model.addAttribute("Participants", instance.getAllParticipants());
            return "Escape The Room/Manage Escape The Room/Status/EscapeTheRoomStatus";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/pause"})
    public String pauseEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            model.addAttribute("SuccessMessage", instance.pauseGame());
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/end"})
    public String endEscapeTheRoom(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id).stopGame(GameState.NOT_FINISHED));
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/delete"})
    public String deleteEscapeTheRoomInstance(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("SuccessMessage", EscapeTheRoomContainer.getInstance().deleteEscapeTheRoom(id));
            return "Feedback Pages/Success";
        } catch (Exception e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/games"})
    public String getEscapeTheRoomGamesPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            model.addAttribute("Escape_The_Room_Instance", EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id));
            model.addAttribute("id", id);
            return "Escape The Room/Games/EscapeTheRoomGames";
        } catch (IllegalArgumentException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/games/MorseCode"})
    public String getEscapeTheRoomMorseCodeInteractionPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(MorseCodeInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                return "Escape The Room/Games/Morse Code/MorseCodeInteractionPage";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/games/MorseCode/submit"})
    public String submitMorseCodeInteractionSolution(@PathVariable(value = "id") String id,
                                                     @RequestParam("value") Optional<String> userInput,
                                                     Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(MorseCodeInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                if (userInput.isPresent()) {
                    Interaction interaction = instance.getInteractionByClassName(MorseCodeInteraction.class.getSimpleName());
                    if (userInput.get().equals(interaction.getInteractionSolution())) {
                        interaction.getInteractionPi().getProxy().setInteractionSolved(Games.ESCAPE_THE_ROOM, id, interaction.getInteractionName());
                        model.addAttribute("SuccessMessage", "You uncovered Samuel's Secret. Congratulations.");
                        return "Feedback Pages/Success";
                    } else {
                        model.addAttribute("ErrorMessage", "It seems you are still not worthy of knowing Samuel's secret...");
                        return "Feedback Pages/Error";
                    }
                } else {
                    model.addAttribute("ErrorMessage", "It seems you are still not worthy of knowing Samuel's secret...");
                    return "Feedback Pages/Error";
                }
            }
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException | RemoteException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            model.addAttribute("StackTrace", sw.toString());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/games/CryptoServo"})
    public String getEscapeTheRoomCryptoServoInteractionPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(CryptoServoInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                model.addAttribute("id", id);
                return "Escape The Room/Games/Crypto Servo/CryptoServoInteractionPage";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/games/CryptoServo/Decipher"})
    public String getEscapeTheRoomCryptoServoInteractionDecipherPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(CryptoServoInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                model.addAttribute("id", id);
                return "Escape The Room/Games/Crypto Servo/Decipher/CryptoServoInteractionDecipherPage";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/games/CryptoServo/Decipher/submit"})
    public String submitCryptoServoInteractionDecipherMessage(@PathVariable(value = "id") String id,
                                                              @RequestParam("value") Optional<String> userInput,
                                                              Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(CryptoServoInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                if (userInput.isPresent()) {
                    Interaction interaction = instance.getInteractionByClassName(CryptoServoInteraction.class.getSimpleName());
                    interaction.getInteractionPi().getProxy().sendMessageToInteraction(Games.ESCAPE_THE_ROOM, id, interaction.getInteractionName(), userInput.get());
                    model.addAttribute("SuccessMessage", "So, did you crack Caesar's secret or are you still on the search?");
                    return "Feedback Pages/Success";
                } else {
                    model.addAttribute("ErrorMessage", "Are you trying to decipher nothing? Well, sorry to say, it's just nothing...");
                    return "Feedback Pages/Error";
                }
            }
        } catch (IllegalArgumentException | IllegalStateException | RemoteException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @GetMapping(value = {"/{id}/games/CryptoServo/solution"})
    public String getEscapeTheRoomCryptoServoInteractionSolutionPage(@PathVariable(value = "id") String id, Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(CryptoServoInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                model.addAttribute("id", id);
                return "Escape The Room/Games/Crypto Servo/Solution/CryptoServoInteractionSolutionPage";
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    @PostMapping(value = {"/{id}/games/CryptoServo/solution/submit"})
    public String submitCryptoServoInteractionSolution(@PathVariable(value = "id") String id,
                                                              @RequestParam("value") Optional<String> userInput,
                                                              Model model) {
        try {
            validateEscapeTheRoomID(id);
            EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(id);
            if (!instance.containsInteraction(CryptoServoInteraction.class.getSimpleName())) {
                throw new IllegalStateException("The Escape The Room with ID " + id + " is not using this interaction.");
            } else {
                if (userInput.isPresent()) {
                    Interaction interaction = instance.getInteractionByClassName(CryptoServoInteraction.class.getSimpleName());
                    String solution = interaction.getInteractionSolution().substring(3, interaction.getInteractionSolution().indexOf(" SF-"));
                    if (userInput.get().equals(solution)) {
                        System.out.println(interaction.getInteractionPi().getProxy().setInteractionSolved(Games.ESCAPE_THE_ROOM, id, interaction.getInteractionName()));
                        model.addAttribute("SuccessMessage", "I guess if you discovered this then Caesar has no more secrecy to his correspondence anymore.");
                        return "Feedback Pages/Success";
                    } else {
                        model.addAttribute("ErrorMessage", "Guess Caesar gets to keep his correspondence a secret after all");
                        return "Feedback Pages/Error";
                    }
                } else {
                    model.addAttribute("ErrorMessage", "Do you think nothing is the solution? Well, I can tell you it isn't, not even the word 'nothing'");
                    return "Feedback Pages/Error";
                }
            }
        } catch (IllegalArgumentException | IllegalStateException | RemoteException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        }
    }

    private void validateEscapeTheRoomID(String ID) throws IllegalArgumentException {
        if (ID == null || ID.trim().equals("")) {
            throw new IllegalArgumentException("The Escape The Room ID cannot be empty or null");
        }
    }
}
