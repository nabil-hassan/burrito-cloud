package net.nh.burrito.controller.api;

import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.entity.rest.IngredientDTO;
import net.nh.burrito.repository.jpa.IngredientRepositoryJPA;
import net.nh.burrito.translation.IngredientAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/ingredients")
public class IngredientAPIController {

    private final IngredientRepositoryJPA repository;
    private final IngredientAssembler assembler;

    @Autowired
    public IngredientAPIController(IngredientRepositoryJPA repository, IngredientAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @PostMapping
    public IngredientDTO createIngredient(@Valid @RequestBody IngredientDTO incoming) {
        IngredientJPA persisted = repository.save(assembler.toJPA(incoming));
        return assembler.toModel(persisted);
    }

    @GetMapping
    public CollectionModel<IngredientDTO> getAllIngredients() {
        List<IngredientJPA> results = new ArrayList<>();
        repository.findAll().forEach(results::add);

        CollectionModel<IngredientDTO> collection = assembler.toCollectionModel(results);
        collection.add(linkTo(methodOn(IngredientAPIController.class).getAllIngredients()).withRel("allIngredients"));

        return collection;
    }
}
