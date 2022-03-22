package pt.ulisboa.tecnico.master_process.databases.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

@Controller
@RequestMapping("/databases")
public class DatabaseController {

    @GetMapping
    public String databaseHomePage() { return "Databases/HomePage"; }

}
