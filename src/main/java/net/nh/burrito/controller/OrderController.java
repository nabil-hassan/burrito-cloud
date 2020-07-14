package net.nh.burrito.controller;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.OrderJDBC;
import net.nh.burrito.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/current")
    public String getCurrent() {
        return "order-form";
    }

    @PostMapping
    public String submitOrder(@Valid OrderJDBC order, Errors errors, SessionStatus sessionStatus, Model model) {
        log.info("Received order: {}", order);
        if (errors.hasErrors()) {
            log.info("Received order has errors: {}", errors.getFieldErrors());
            return "order-form";
        }
        OrderJDBC saved = orderRepository.create(order);
        log.info("Order: {} saved successfully", saved.getId());
        sessionStatus.setComplete();
        model.addAttribute("message", "Your order has been processed successfully. See you soon!");
        return "redirect:/?orderComplete=true";
    }

}
