package com.nextel.techstack.navigation.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CountryGraphNode {
    final String cca3;
    final List<CountryGraphNode> neighbours = new ArrayList<>();

    public CountryGraphNode(String cca3) {
        this.cca3 = cca3;
    }

    public void addNeighbour(CountryGraphNode neighbour){
        if(neighbours.contains(neighbour)){
            return;
        }
        neighbours.add(neighbour);
        neighbour.addNeighbour(this);
    }

    public String getCca3() {
        return cca3;
    }

    public List<CountryGraphNode> getNeighbours() {
        return neighbours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryGraphNode countryGraphNode = (CountryGraphNode) o;
        return cca3.equals(countryGraphNode.cca3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cca3);
    }
}
