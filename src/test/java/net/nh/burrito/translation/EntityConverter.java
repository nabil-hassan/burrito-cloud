package net.nh.burrito.translation;

import net.nh.burrito.entity.IngredientType;
import net.nh.burrito.entity.jdbc.BurritoJDBC;
import net.nh.burrito.entity.jdbc.IngredientJDBC;
import net.nh.burrito.entity.jdbc.OrderJDBC;
import net.nh.burrito.entity.jpa.BurritoJPA;
import net.nh.burrito.entity.jpa.IngredientJPA;
import net.nh.burrito.entity.jpa.OrderJPA;
import net.nh.burrito.entity.rest.IngredientDTO;
import net.nh.burrito.repository.jpa.IngredientRepositoryJPA;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to convert between the various entity types used in this sandbox.
 */
@Component
public class EntityConverter {

    private final IngredientRepositoryJPA ingredientRepository;

    public EntityConverter(IngredientRepositoryJPA ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public BurritoJPA toBurritoJPA(BurritoJDBC source) {
        List<IngredientJPA> jpaList = new ArrayList<>();
        Iterable<IngredientJPA> ingredientsIterable = ingredientRepository.findAllById(source.getIngredients());
        ingredientsIterable.forEach(jpaList::add);
        return BurritoJPA.builder().id(source.getId()).name(source.getName()).createdAt(source.getCreatedAt()).ingredients(new ArrayList<>(jpaList)).build();
    }

    public IngredientJPA toIngredientJPA(IngredientJDBC source) {
        return IngredientJPA.builder().id(source.getId()).name(source.getName()).type(IngredientType.valueOf(source.getType().name())).build();
    }

    public OrderJPA toOrderJPA(OrderJDBC source) {
        List<BurritoJPA> burritos = source.getBurritos().stream().map(this::toBurritoJPA).collect(Collectors.toList());
        return OrderJPA.builder().id(source.getId()).name(source.getOrderName())
                .street(source.getStreet()).town(source.getTown()).county(source.getCounty()).postcode(source.getPostcode())
                .creditCardNo(source.getCreditCardNo()).creditCardCCV(source.getCreditCardCCV()).creditCardExpiryDate(source.getCreditCardExpiryDate())
                .burritos(burritos).build();
    }

    public IngredientJPA toIngredientJPA(IngredientDTO source) {
        return IngredientJPA.builder().id(source.getId()).name(source.getName()).type(source.getType()).build();
    }

    public IngredientDTO toIngredientDTO(IngredientJPA persisted) {
        return IngredientDTO.builder().id(persisted.getId()).name(persisted.getName()).type(persisted.getType()).build();
    }
}
