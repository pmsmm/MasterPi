package pt.ulisboa.tecnico.master_process.interactionpies.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;

@Controller
@RequestMapping("/interactionpies/all")
public class ShowAllInteractionPiesController {

    @GetMapping
    public String allHomePage(Model model) {
        model.addAttribute("InteractionPies", InteractionPiesContainer.getInteractionPies());
        return "Interaction Pies/All/AllInteractionPies";
    }

}
