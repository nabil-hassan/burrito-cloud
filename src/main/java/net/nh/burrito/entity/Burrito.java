package net.nh.burrito.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class Burrito {
    private String name;
    private List<String> ingredients;
}
