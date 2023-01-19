package com.nextel.techstack.navigation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToFindRouteException extends RuntimeException {
    public UnableToFindRouteException(String origin, String destination) {
        super("Unable to find route between: " + origin + " and " + destination);
    }
}
