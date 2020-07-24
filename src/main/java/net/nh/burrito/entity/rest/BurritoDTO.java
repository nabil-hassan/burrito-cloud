package net.nh.burrito.entity.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BurritoDTO extends RepresentationModel<BurritoDTO> {

    private Date createdAt;
    private String name;
    private List<IngredientDTO> ingredients;

}
