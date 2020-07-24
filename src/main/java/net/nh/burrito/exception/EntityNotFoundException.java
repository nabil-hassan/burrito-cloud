package net.nh.burrito.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, Object id) {
        super("Unable to find " + type + " with id: " + id);
    }
}
