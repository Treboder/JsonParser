package com.example.JsonParser.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    private String id;
    private String name;
    private String birthYear;
    private String deathYear;
    private String homeCountry;
    private String achievements;
    private String[] keyWords;

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

}
