package net.nh.burrito.entity.jdbc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientJDBC {
    private String id, name;

    @ToString.Exclude
    private Type type;

    public enum Type {
        MEAT, SAUCE, VEGETABLE, WRAP;
    }
}
