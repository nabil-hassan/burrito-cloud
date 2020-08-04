package net.nh.burrito.controller.api;

import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.rest.BurritoDTO;
import net.nh.burrito.entity.rest.BurritoRequest;
import net.nh.burrito.entity.rest.IngredientDTO;
import net.nh.burrito.exception.EntityNotFoundException;
import net.nh.burrito.repository.jpa.BurritoPageableRepositoryJPA;
import net.nh.burrito.service.BurritoService;
import net.nh.burrito.translation.BurritoAssembler;
import net.nh.burrito.translation.IngredientAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/burritos")
public class BurritoAPIController {

    private final BurritoAssembler assembler;
    private final PagedResourcesAssembler<BurritoJPA> pageableAssembler;
    private final IngredientAssembler ingredientAssembler;
    private final BurritoPageableRepositoryJPA repository;
    private final BurritoService service;

    @Autowired
    public BurritoAPIController(BurritoPageableRepositoryJPA repository, BurritoAssembler assembler, BurritoService service,
                                PagedResourcesAssembler<BurritoJPA> pageableAssembler, IngredientAssembler ingredientAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pageableAssembler = pageableAssembler;
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

    @GetMapping
    public PagedModel<BurritoDTO> getAllBurritos(Pageable pageable) {
        Page<BurritoJPA> entities = repository.findAll(pageable);
        return pageableAssembler.toModel(entities, assembler);
    }

}
