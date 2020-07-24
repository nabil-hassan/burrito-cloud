package net.nh.burrito.translation;

import net.nh.burrito.controller.api.BurritoAPIController;
import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.rest.BurritoDTO;
import net.nh.burrito.entity.rest.IngredientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BurritoAssembler extends RepresentationModelAssemblerSupport<BurritoJPA, BurritoDTO> {

    private final IngredientAssembler ingredientAssembler;

    @Autowired
    public BurritoAssembler(IngredientAssembler ingredientAssembler) {
        super(BurritoAPIController.class, BurritoDTO.class);
        this.ingredientAssembler = ingredientAssembler;
    }

    @Override
    public BurritoDTO toModel(BurritoJPA entity) {
        Long id = entity.getId();
        BurritoDTO result = createModelWithId(id, entity);
        // add a link to obtain all ingredients for the Burrito
        result.add(linkTo(methodOn(BurritoAPIController.class).getIngredients(id)).withRel("ingredients").expand(id));
        return result;
    }

    @Override
    protected BurritoDTO instantiateModel(BurritoJPA entity) {
        List<IngredientDTO> ingredients = entity.getIngredients().stream().map(ingredientAssembler::toModel).collect(toList());
        return BurritoDTO.builder().createdAt(entity.getCreatedAt()).name(entity.getName()).ingredients(ingredients).build();
    }
}
