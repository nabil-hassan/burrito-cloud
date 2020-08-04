package net.nh.burrito.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "burritos")
public class BurritoDTO extends RepresentationModel<BurritoDTO> {

    private Date createdAt;
    private String name;
    private List<IngredientDTO> ingredients;

}
