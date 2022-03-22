package pt.ulisboa.tecnico.master_process.interactionpies.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;

@Controller
@RequestMapping("/interactionpies/remove")
public class RemoveInteractionPiController {

    @PostMapping(value = "/{InteractionPiAddress}/{Port}")
    public String removeNewInteractionPi(@PathVariable("InteractionPiAddress")String ipAddress, @PathVariable("Port")int port, Model model) {
        try {
            if(ipAddress == null) {
                throw new IllegalArgumentException("Please introduce a valid IP address!");
            }

            //This should be removed later on since the port shall be a fixed one
            if(port == 0) {
                throw new IllegalArgumentException("Please introduce a valid Port!");
            }

            model.addAttribute("SuccessMessage", InteractionPiesContainer.getInstance().removeInteractionPi(ipAddress));
            return "Feedback Pages/Success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "Feedback Pages/Error";
        } catch (RemoteException remoteException) {
            model.addAttribute("ErrorMessage", remoteException.getMessage());
            StringWriter sw = new StringWriter();
            remoteException.printStackTrace(new PrintWriter(sw));
            model.addAttribute("StackTrace", sw.toString());
            return "Feedback Pages/Error";
        }
    }

}
