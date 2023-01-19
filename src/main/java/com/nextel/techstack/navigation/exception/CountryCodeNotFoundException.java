package com.nextel.techstack.navigation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CountryCodeNotFoundException extends RuntimeException {

    public CountryCodeNotFoundException(String originCca3, String destinationCca3) {
        super("Unable to find cca3: " + originCca3 + "/" + destinationCca3);
    }
}
