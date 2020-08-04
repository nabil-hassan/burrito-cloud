package net.nh.burrito.controller.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Root API controller. Provides a HATEOAS compatible listing of available resources.
 */
@RestController
@RequestMapping("/api")
public class RootApiController {

    @GetMapping
    private Link getRoot() {
        return linkTo(methodOn(IngredientAPIController.class).getAllIngredients()).withRel("ingredients");
    }




}
