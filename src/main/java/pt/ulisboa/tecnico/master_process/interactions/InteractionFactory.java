package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

public class InteractionFactory {

    public static Interaction getInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        switch (interactionName) {
            case "Binary Number Interaction":
                return new BinaryNumberInteraction(pi, interactionName, interactionSolution, phase);
            case "Morse Code":
                return new MorseCodeInteraction(pi, interactionName, interactionSolution, phase);
            case "Electric Panel Interaction":
                return new ElectricPanelInteraction(pi, interactionName, interactionSolution, phase);
            case "RGB LED Interaction":
                return new RGBLedInteraction(pi, interactionName, interactionSolution, phase);
            case "Ordered Numbers Interaction":
                return new OrderedNumbersInteraction(pi, interactionName, interactionSolution, phase);
            case "Crypto Servo Interaction":
                return new CryptoServoInteraction(pi, interactionName, interactionSolution, phase);
            case "Safe Crack Interaction":
                return new SafeCrackInteraction(pi, interactionName, interactionSolution, phase);
            case "Shapes Weight Interaction":
                return new ShapesWeightInteraction(pi, interactionName, interactionSolution, phase);
            case "Simon Says Interaction":
                return new SimonSaysInteraction(pi, interactionName, interactionSolution, phase);
            case "Light Sensor Interaction":
                return new LightSensorInteraction(pi, interactionName, interactionSolution, phase);
            case "Coordinated RFID Cards Interaction":
                return new CoordinatedRFIDCardsInteraction(pi, interactionName, interactionSolution, phase);
            case "Ultrasonic Distance Interaction":
                return new UltrasonicDistanceInteraction(pi, interactionName, interactionSolution, phase);
            case "Keypad Interaction":
                return new KeypadInteraction(pi, interactionName, interactionSolution, phase);
            default:
                throw new IllegalArgumentException("No Interaction with name " + interactionName);
        }
    }

}
