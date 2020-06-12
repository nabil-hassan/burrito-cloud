package net.nh.burrito.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Burrito {
    private Long id;
    private String name;
    @Builder.Default
    private List<String> ingredients = new ArrayList<>();

    public static void main(String[] args) {
        Burrito viaBuilder = Burrito.builder().build();
        assert(viaBuilder.getIngredients() != null && viaBuilder.getIngredients().isEmpty());

        Burrito viaConstructor = new Burrito();
        viaConstructor.setName("Hi");
        assert(viaConstructor.getIngredients() != null && viaConstructor.getIngredients().isEmpty());
    }

}
