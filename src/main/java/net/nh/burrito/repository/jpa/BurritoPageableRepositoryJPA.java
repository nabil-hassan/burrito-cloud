package net.nh.burrito.repository.jpa;

import net.nh.burrito.entity.jpa.BurritoJPA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BurritoPageableRepositoryJPA extends PagingAndSortingRepository<BurritoJPA, Long> {

    Page<BurritoJPA> findAllByName(String name, Pageable pageable);
    Slice<BurritoJPA> findAllByNameLike(String name, Pageable pageable);

}
