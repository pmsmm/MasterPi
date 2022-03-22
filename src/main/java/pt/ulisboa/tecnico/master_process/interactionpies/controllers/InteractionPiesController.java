package pt.ulisboa.tecnico.master_process.interactionpies.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/interactionpies")
public class InteractionPiesController {

    @GetMapping
    public String raspberryPiesManagementHomePage() {
        return "Interaction Pies/InteractionPies";
    }

}
