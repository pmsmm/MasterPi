package pt.ulisboa.tecnico.master_process.escapetheroom.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

@Controller
@RequestMapping("/escapetheroom")
public class EscapeTheRoomController {

    @GetMapping
    public String escapeTheRoomHomePage() {
        return "Escape The Room/EscapeTheRoom";
    }

}
