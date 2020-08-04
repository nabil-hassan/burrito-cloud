package net.nh.burrito.entity.rest;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
public class BurritoRequest {
    private String name;
    private List<String> ingredientIds;
}
