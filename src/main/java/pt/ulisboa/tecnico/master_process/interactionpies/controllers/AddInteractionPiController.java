package pt.ulisboa.tecnico.master_process.interactionpies.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;

import java.io.PrintWriter;
import java.io.StringWriter;


@Controller
@RequestMapping("/interactionpies/add")
public class AddInteractionPiController {

    @GetMapping
    public String addHomePage() {
        return "Interaction Pies/Add/AddInteractionPi";
    }

    @PostMapping(value = "/submit")
    public String addNewInteractionPi(@RequestParam("ipAddress") String ipAddress, @RequestParam("port") int port, Model model) {
        try {
            if(ipAddress == null) {
                throw new Exception("Please introduce a valid IP address!");
            }

            //This should be removed later on since the port shall be a fixed one
            if(port == 0) {
                throw new Exception("Please introduce a valid Port!");
            }

            model.addAttribute("SuccessMessage", InteractionPiesContainer.getInstance().addInteractionPi(ipAddress, port));
            return "Feedback Pages/Success";
        } catch (Exception exception) {
            model.addAttribute("ErrorMessage", exception.getMessage());
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            model.addAttribute("StackTrace", sw.toString());
            return "Feedback Pages/Error";
        }
    }

}
