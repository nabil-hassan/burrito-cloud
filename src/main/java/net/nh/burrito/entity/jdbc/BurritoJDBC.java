package net.nh.burrito.entity.jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BurritoJDBC {
    private Long id;
    private String name;
    @Builder.Default
    // List of ingredient ids
    private List<String> ingredients = new ArrayList<>();
    private Date createdAt;

}
