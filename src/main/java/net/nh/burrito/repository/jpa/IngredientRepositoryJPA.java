package net.nh.burrito.repository.jpa;

import net.nh.burrito.entity.jpa.IngredientJPA;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepositoryJPA extends CrudRepository<IngredientJPA, String> {

}
