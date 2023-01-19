package com.nextel.techstack.navigation.controller;

import com.nextel.techstack.navigation.exception.CountryCodeNotFoundException;
import com.nextel.techstack.navigation.model.CountryGraphNode;
import com.nextel.techstack.navigation.exception.UnableToFindRouteException;
import com.nextel.techstack.navigation.service.NavigationService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class NavigationController {


    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/navigation/{origin}/{destination}")
    public List<String> getRoute(@PathVariable String origin, @PathVariable String destination) {
        CountryGraphNode originNode = navigationService.graphs.get(origin);
        CountryGraphNode destinationNode = navigationService.graphs.get(destination);
        if (originNode == null || destinationNode == null) {
            throw new CountryCodeNotFoundException(origin, destination);
        }
        List<String> route = navigationService.getRoute(originNode, destinationNode, new ArrayList<>(), new HashMap<>());
        if (route == null) {
            throw new UnableToFindRouteException(origin, destination);
        }
        return route;
    }
}
