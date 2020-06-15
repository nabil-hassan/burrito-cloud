package net.nh.burrito.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static java.lang.Boolean.TRUE;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@RequestParam("orderComplete") Boolean orderComplete, Model model) {
        if (TRUE.equals(orderComplete)) {
            model.addAttribute("message", "Thanks for your order, see you soon!");
        }
        return "home";
    }

}
