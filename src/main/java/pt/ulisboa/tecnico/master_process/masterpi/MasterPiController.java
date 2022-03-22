package pt.ulisboa.tecnico.master_process.masterpi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {" * ", "/", "/home"})
public class MasterPiController {

    @GetMapping
    public String masterPiHomePage() {
        return "Peddy Room System/index";
    }

}
