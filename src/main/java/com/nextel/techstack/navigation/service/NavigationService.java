package com.nextel.techstack.navigation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextel.techstack.navigation.model.CountryEntityBasic;
import com.nextel.techstack.navigation.model.CountryGraphNode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NavigationService {

    private List<CountryEntityBasic> countriesWithBorders;
    public Map<String, CountryGraphNode> graphs = new HashMap<>();
    private RestTemplate restTemplate = new RestTemplate();

    private final String COUNTRIES_INFO_URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";

    @PostConstruct
    private void init() {
        String response = restTemplate.getForObject(COUNTRIES_INFO_URL, String.class);
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectMapper om = new ObjectMapper();
        List<CountryEntityBasic> countries;
        try {
            countriesWithBorders = mapper.readValue(response, new TypeReference<List<CountryEntityBasic>>() {
            });
            System.out.println("Loaded countries");
            initializeGraphs();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void initializeGraphs() {
        for (CountryEntityBasic singleCountryWithBorders : countriesWithBorders) {
            String countryCode = singleCountryWithBorders.getCca3();
            CountryGraphNode country = graphs.computeIfAbsent(countryCode, CountryGraphNode::new);

            List<String> borderCountryCodes = singleCountryWithBorders.getBorders();
            if (borderCountryCodes == null || borderCountryCodes.isEmpty()) {
                continue;
            }

            for (String borderCountryCode : borderCountryCodes) {
                CountryGraphNode borderCountryCountryGraphNode = graphs.computeIfAbsent(borderCountryCode, CountryGraphNode::new);
                country.addNeighbour(borderCountryCountryGraphNode);
            }
        }

        System.out.println("Initialized the graph with a size of " + graphs.size());
    }

    public List<String> getRoute(CountryGraphNode originNode, CountryGraphNode destinationNode, List<String> previousRoute, Map<CountryGraphNode, Integer> processedNodes) {
        if (originNode.getNeighbours() == null || originNode.getNeighbours().isEmpty()) {
            // reached the end and found nothing;
            return null;
        }
        if (wasAlreadyProcessedWithABetterRoute(originNode, previousRoute, processedNodes)) {
            // Already processed this node and didn't find a connection to the destinationNode
            return null;
        }
        processedNodes.put(originNode, previousRoute.size());

        List<String> route = new ArrayList<>(previousRoute);
        route.add(originNode.getCca3());

        List<CountryGraphNode> neighbours = originNode.getNeighbours();
        List<List<String>> potentialRouteLists = new ArrayList<>();
        for (CountryGraphNode neighbour : neighbours) {
            if (wasAlreadyProcessedWithABetterRoute(neighbour, route, processedNodes)) {
                // Already processed this neighbour node and didn't find a connection to the destinationNode, skipping it
                continue;
            }
            if (neighbour.equals(destinationNode)) {
                // The neighbour node is the destination node
                route.add(neighbour.getCca3());
                return route;
            }
            List<String> routeList = getRoute(neighbour, destinationNode, route, processedNodes);
            if (routeList != null) {
                // found potential route
                potentialRouteLists.add(routeList);
            }
        }
        if (potentialRouteLists.isEmpty()) {
            // No neighbour had a land connection to the destination country
            return null;
        }
        List<String> bestRoute = potentialRouteLists.get(0);
        for (int i = 1; i < potentialRouteLists.size(); i++) {
            if (bestRoute.size() > potentialRouteLists.get(i).size()) {
                bestRoute = potentialRouteLists.get(i);
            }
        }
        return bestRoute;
    }

    private boolean wasAlreadyProcessedWithABetterRoute(CountryGraphNode originNode, List<String> previousRoute, Map<CountryGraphNode, Integer> processedNodes) {
        return processedNodes.containsKey(originNode) && processedNodes.get(originNode) < previousRoute.size();
    }
}
