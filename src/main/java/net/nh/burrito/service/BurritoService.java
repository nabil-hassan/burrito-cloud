package net.nh.burrito.service;

import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.entity.rest.BurritoRequest;
import net.nh.burrito.exception.EntityNotFoundException;
import net.nh.burrito.repository.jpa.BurritoRepositoryJPA;
import net.nh.burrito.repository.jpa.IngredientRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BurritoService {

    private final BurritoRepositoryJPA burritoRepository;
    private final IngredientRepositoryJPA ingredientRepository;

    public BurritoService(BurritoRepositoryJPA burritoRepository, IngredientRepositoryJPA ingredientRepository) {
        this.burritoRepository = burritoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public BurritoJPA createBurrito(BurritoRequest request) {
        List<IngredientJPA> ingredients = request.getIngredientIds().stream()
                .map(id -> ingredientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("ingredient", id)))
                .collect(Collectors.toList());

        BurritoJPA newBurrito = BurritoJPA.builder().name(request.getName()).ingredients(ingredients).build();
        return burritoRepository.save(newBurrito);
    }

}
