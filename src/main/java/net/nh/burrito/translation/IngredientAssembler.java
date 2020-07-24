package net.nh.burrito.translation;

import net.nh.burrito.controller.api.IngredientAPIController;
import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.entity.rest.BurritoDTO;
import net.nh.burrito.entity.rest.IngredientDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class IngredientAssembler extends RepresentationModelAssemblerSupport<IngredientJPA, IngredientDTO> {

    public IngredientAssembler() {
        super(IngredientAPIController.class, IngredientDTO.class);
    }

    @Override
    public IngredientDTO toModel(IngredientJPA jpa) {
        return createModelWithId(jpa.getId(), jpa);
    }

    @Override
    protected IngredientDTO instantiateModel(IngredientJPA entity) {
        return IngredientDTO.builder().id(entity.getId()).name(entity.getName()).type(entity.getType()).build();
    }

    public IngredientJPA toJPA(IngredientDTO dto) {
        return IngredientJPA.builder().type(dto.getType()).name(dto.getName()).id(dto.getId()).build();
    }
}
