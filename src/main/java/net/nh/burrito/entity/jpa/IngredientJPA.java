package net.nh.burrito.entity.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ingredient")
public class IngredientJPA {

    @Id
    private String id;

    private String name;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Type {
        MEAT, SAUCE, VEGETABLE, WRAP;
    }
}
