package net.nh.burrito.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Ingredient {
    private final String id, name;

    @ToString.Exclude
    private final Type type;

    public enum Type {
        MEAT, SAUCE, VEGETABLE, WRAP;
    }
}
