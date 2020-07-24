package net.nh.burrito.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nh.burrito.entity.IngredientType;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "ingredients")
public class IngredientDTO extends RepresentationModel<IngredientDTO> {

    @NotNull
    @Size(min = 0, max = 4)
    private String id;

    @NotNull
    @Size(min = 0, max = 25)
    private String name;

    @NotNull
    private IngredientType type;

}
