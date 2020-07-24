package net.nh.burrito.controller.api;

import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.rest.BurritoDTO;
import net.nh.burrito.entity.rest.BurritoRequest;
import net.nh.burrito.entity.rest.IngredientDTO;
import net.nh.burrito.exception.EntityNotFoundException;
import net.nh.burrito.repository.jpa.BurritoRepositoryJPA;
import net.nh.burrito.service.BurritoService;
import net.nh.burrito.translation.BurritoAssembler;
import net.nh.burrito.translation.IngredientAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/burritos")
public class BurritoAPIController {

    private final BurritoAssembler assembler;
    private final IngredientAssembler ingredientAssembler;
    private final BurritoRepositoryJPA repository;
    private final BurritoService service;


    @Autowired
    public BurritoAPIController(BurritoRepositoryJPA repository, BurritoAssembler assembler, IngredientAssembler ingredientAssembler, BurritoService service) {
        this.repository = repository;
        this.assembler = assembler;
        this.ingredientAssembler = ingredientAssembler;
        this.service = service;
    }

    @PostMapping
    public BurritoDTO createBurrito(@RequestBody BurritoRequest request) {
        BurritoJPA burrito = service.createBurrito(request);
        return assembler.toModel(burrito);
    }

    @GetMapping("/{id}")
    public BurritoDTO getById(@PathVariable("id") Long id) {
        BurritoJPA burrito = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("burrito", id));
        return assembler.toModel(burrito);
    }

    @GetMapping("/{id}/ingredients")
    public CollectionModel<IngredientDTO> getIngredients(@PathVariable Long id) {
        BurritoJPA burrito = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("burrito", id));
        CollectionModel<IngredientDTO> ingredientCollection = ingredientAssembler.toCollectionModel(burrito.getIngredients());
        ingredientCollection.add(linkTo(methodOn(this.getClass()).getIngredients(id)).withSelfRel().expand(id));
        return ingredientCollection;
    }


}
