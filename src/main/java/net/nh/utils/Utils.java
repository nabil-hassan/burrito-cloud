package net.nh.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <T> List<T> toList(Iterable<T> source) {
        List<T> result = new ArrayList<>();
        source.forEach(result::add);
        return result;
    }

}
