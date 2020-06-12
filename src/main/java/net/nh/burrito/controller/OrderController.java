package net.nh.burrito.controller;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    @GetMapping("/current")
    public String getCurrent(Model model) {
        model.addAttribute("order", new Order());
        return "order-form";
    }

    @PostMapping
    public String submitOrder(@Valid Order order, Errors errors) {
        if (errors.hasErrors()) {
            log.info("Received order has errors: {}", errors.getFieldErrors());
            return "order-form";
        }
        log.info("Received order: {}", order);
        return "redirect:/orders/current";
    }

}
