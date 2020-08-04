package net.nh.burrito;

import net.nh.burrito.entity.rest.IngredientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Hop;
import org.springframework.hateoas.client.JsonPathLinkDiscoverer;
import org.springframework.hateoas.client.Traverson;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class TraversonSandbox {

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
    }

    @Test
    void tryTraverson() throws InterruptedException {
        // n.b. the base url must point to an endpoint that returns media type application/hal+json
        // unless your api root provides a listing of all available resources, you must point to a HATEOAS enabled endpoint such as this
        Traverson traverson = new Traverson(URI.create(baseUrl + "/ingredients"), MediaTypes.HAL_JSON);

        // find collections of ingredients - needs a parameterised type reference
        ParameterizedTypeReference<CollectionModel<IngredientDTO>> ingredientType = new ParameterizedTypeReference<>() {};
        CollectionModel<IngredientDTO> ingredientDTOS = traverson.follow().toObject(ingredientType);

        // find the first ingredient in the collection
        IngredientDTO ingredientById = traverson.follow("$._embedded.ingredients[0]._links.self.href").toObject(IngredientDTO.class);

        // find the resource URL of a specific ingredient - this may be useful if you plan on constructing a manual request e.g. to update the node
        String ingredientUrl  = traverson.follow("$._embedded.ingredients[0]._links.self.href").toObject(IngredientDTO.class).toString();

        // find the URL for a specific ingredient with given name
        List<IngredientDTO> ingredients = new ArrayList<>();
        ingredientDTOS.forEach(ingredients::add);
        Optional<IngredientDTO> steakOpt = ingredients.stream().filter(i -> i.getName().equals("Steak")).findFirst();
        if (steakOpt.isPresent()) {
            Optional<Link> self = steakOpt.get().getLink("self");
            System.out.println(self.get().getHref());
        }

    }
}
