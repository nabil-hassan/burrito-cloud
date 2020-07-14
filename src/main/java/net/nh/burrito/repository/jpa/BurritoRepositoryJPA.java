package net.nh.burrito.repository.jpa;

import net.nh.burrito.entity.jpa.BurritoJPA;
import org.springframework.data.repository.CrudRepository;

public interface BurritoRepositoryJPA extends CrudRepository<BurritoJPA, Long> {
}
