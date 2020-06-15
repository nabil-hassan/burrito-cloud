package net.nh.burrito.controller;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Ingredient;
import net.nh.burrito.entity.Order;
import net.nh.burrito.repository.BurritoRepository;
import net.nh.burrito.repository.IngredientRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignController implements InitializingBean {

    private Map<String, List<Ingredient>> ingredientsByType;
    private final IngredientRepository ingredientRepository;
    private final BurritoRepository burritoRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        this.ingredientsByType = ingredients.stream().collect(Collectors.groupingBy(i -> i.getType().name().toLowerCase()));
    }

    @Autowired
    public DesignController(IngredientRepository ingredientRepository, BurritoRepository burritoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.burritoRepository = burritoRepository;
    }

    @ModelAttribute(name = "design")
    public Burrito design() {
        log.info("Create new burrito object");
        return new Burrito();
    }

    @ModelAttribute(name = "order")
    public Order order() {
        log.info("Create new order object");
        return new Order();
    }

    @GetMapping
    public String getDesign(Model model) {
        ingredientsByType.entrySet().forEach(e -> model.addAttribute(e.getKey(), e.getValue()));
        return "design";
    }

    @PostMapping
    public String submitDesign(Burrito design, @ModelAttribute Order order) {
        log.info("Received design: {} with order: {}", design, order);
        Burrito savedDesign = burritoRepository.create(design);
        order.getBurritos().add(savedDesign);
        return "redirect:/orders/current";
    }

}
