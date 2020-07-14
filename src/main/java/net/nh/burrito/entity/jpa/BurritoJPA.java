package net.nh.burrito.entity.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "burrito")
public class BurritoJPA {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "created_at")
    private Date createdAt;

    @Builder.Default
    @ManyToMany(targetEntity= IngredientJPA.class, fetch = FetchType.EAGER)
    @JoinTable(name = "burrito_ingredients",
            joinColumns = @JoinColumn(name = "burrito_id", updatable = false, nullable = false),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id", updatable = false, nullable = false))
    @Size(min=1, message="You must choose at least 1 ingredient")
    private List<IngredientJPA> ingredients = new ArrayList<>();

    @PrePersist
    void createdAt() {
        this.createdAt = new Date();
    }
}
