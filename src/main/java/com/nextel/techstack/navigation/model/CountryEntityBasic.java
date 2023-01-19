package com.nextel.techstack.navigation.model;

import java.util.List;

public class CountryEntityBasic {
    private String cca3;
    private List<String> borders;

    public String getCca3() {
        return cca3;
    }

    public void setCca3(String cca3) {
        this.cca3 = cca3;
    }

    public List<String> getBorders() {
        return borders;
    }

    public void setBorders(List<String> borders) {
        this.borders = borders;
    }
}
