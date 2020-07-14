package net.nh.burrito.controller;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
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

    private Map<String, List<IngredientJDBC>> ingredientsByType;
    private final IngredientRepository ingredientRepository;
    private final BurritoRepository burritoRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<IngredientJDBC> ingredients = ingredientRepository.findAll();
        this.ingredientsByType = ingredients.stream().collect(Collectors.groupingBy(i -> i.getType().name().toLowerCase()));
    }

    @Autowired
    public DesignController(IngredientRepository ingredientRepository, BurritoRepository burritoRepository) {
        this.ingredientRepository = ingredientRepository;
        this.burritoRepository = burritoRepository;
    }

    @ModelAttribute(name = "design")
    public BurritoJDBC design() {
        log.info("Create new burrito object");
        return new BurritoJDBC();
    }

    @ModelAttribute(name = "order")
    public OrderJDBC order() {
        log.info("Create new order object");
        return new OrderJDBC();
    }

    @GetMapping
    public String getDesign(Model model) {
        ingredientsByType.entrySet().forEach(e -> model.addAttribute(e.getKey(), e.getValue()));
        return "design";
    }

    @PostMapping
    public String submitDesign(BurritoJDBC design, @ModelAttribute OrderJDBC order) {
        log.info("Received design: {} with order: {}", design, order);
        BurritoJDBC savedDesign = burritoRepository.create(design);
        order.getBurritos().add(savedDesign);
        return "redirect:/orders/current";
    }

}
