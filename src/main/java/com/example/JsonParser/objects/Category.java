package com.example.JsonParser.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {

    private String categoryID;
    private String categoryName;
    private Person[] people;

    public Category(String categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.people = new Person[10];
    }

    @Override
    public String toString() {
        return categoryName + " (" + categoryID + ")";
    }
}
