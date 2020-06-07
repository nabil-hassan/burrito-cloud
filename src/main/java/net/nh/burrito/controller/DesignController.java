package net.nh.burrito.controller;

import lombok.extern.slf4j.Slf4j;
import net.nh.burrito.entity.Burrito;
import net.nh.burrito.entity.Ingredient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static net.nh.burrito.entity.Ingredient.Type.MEAT;
import static net.nh.burrito.entity.Ingredient.Type.SAUCE;
import static net.nh.burrito.entity.Ingredient.Type.VEGETABLE;
import static net.nh.burrito.entity.Ingredient.Type.WRAP;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignController {

    private static final Map<String, List<Ingredient>> ingredientsByType;
    static {
        ingredientsByType = new LinkedHashMap<>();

        List<Ingredient> wraps = asList(Ingredient.builder().type(WRAP).id("FLTO").name("Flour Tortilla").build(),
                Ingredient.builder().type(WRAP).id("COTO").name("Corn Tortilla").build());

        List<Ingredient> meat = asList(Ingredient.builder().type(MEAT).id("CHCK").name("Chicken").build(),
                Ingredient.builder().type(MEAT).id("BSTK").name("Steak").build(),
                Ingredient.builder().type(MEAT).id("TRKY").name("Turkey").build());

        List<Ingredient> vegetables = asList(Ingredient.builder().type(VEGETABLE).id("TOMO").name("Tomato").build(),
                Ingredient.builder().type(VEGETABLE).id("ONIN").name("Onion").build(),
                Ingredient.builder().type(VEGETABLE).id("PPPR").name("Pepper").build());

        List<Ingredient> sauces = asList(Ingredient.builder().type(SAUCE).id("KTCH").name("Ketchup").build(),
                Ingredient.builder().type(SAUCE).id("MAYO").name("Mayonaise").build());

        ingredientsByType.put(WRAP.name().toLowerCase(), wraps);
        ingredientsByType.put(MEAT.name().toLowerCase(), meat);
        ingredientsByType.put(VEGETABLE.name().toLowerCase(), vegetables);
        ingredientsByType.put(SAUCE.name().toLowerCase(), sauces);
    }

    @GetMapping
    public String getDesign(Model model) {
        ingredientsByType.entrySet().forEach(e -> model.addAttribute(e.getKey(), e.getValue()));
        model.addAttribute("design", new Burrito());
        return "design";
    }

    @PostMapping
    public String submitDesign(Burrito design) {
        log.info("Received design: {}", design);
        // TODO: persist in the db
        return "redirect:/orders/current";
    }
}
