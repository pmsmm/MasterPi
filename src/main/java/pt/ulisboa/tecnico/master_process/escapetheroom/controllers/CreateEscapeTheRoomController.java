package pt.ulisboa.tecnico.master_process.escapetheroom.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;

import java.util.logging.Logger;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

@Controller
@RequestMapping("/escapetheroom/new")
public class CreateEscapeTheRoomController {

    private static final Logger LOGGER = Logger.getLogger(CreateEscapeTheRoomController.class.getName());

    @GetMapping
    public ModelAndView createNewEscapeTheRoom(ModelMap model) {
        EscapeTheRoom instance = EscapeTheRoomContainer.getInstance().createNewEscapeTheRoom();
        return new ModelAndView("redirect:/escapetheroom/instances/" + instance.getCreationDate(), model);
    }

}
