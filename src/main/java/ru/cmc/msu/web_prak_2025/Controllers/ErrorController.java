package ru.cmc.msu.web_prak_2025.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String handleError(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("message", message);
        return "error";
    }
}