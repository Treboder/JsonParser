package com.example.JsonParser.functions;

import com.example.JsonParser.objects.Category;
import com.example.JsonParser.objects.DataSet;
import com.example.JsonParser.objects.Person;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Analyzer {

    private static final Analyzer instance = new Analyzer();

    private boolean isInitialized = false;

    private HashMap<String, Category> categoryHashMap = new HashMap<>();
    private HashMap<String, List<String>> namesHashmapWithListOfCategories = new HashMap<>();
    private List<String> multiCategoryPeopleList = new ArrayList<>();
    private List<String> duplicatePeopleList = new ArrayList<>();

    // private constructor to avoid call from others
    private Analyzer(){}

    public static Analyzer getInstance() {
        return instance;
    }

    public void initialize(HashMap<String, Category> categoryHashMap) {
        this.categoryHashMap = categoryHashMap;
        this.namesHashmapWithListOfCategories = createMapWithNamesAndListOfCategories();
        this.multiCategoryPeopleList = getListOfMultiCategoryPeople();
        this.duplicatePeopleList = getListOfDuplicates();
        this.isInitialized = true;
    }

    private HashMap<String, List<String>> createMapWithNamesAndListOfCategories() {
        HashMap<String, List<String>> hashmap = new HashMap<>();
        for (String categoryID : categoryHashMap.keySet().stream().toList()) {
            Category c = categoryHashMap.get(categoryID);
            for (Person p : c.getPeople()) {
                // add entry
                if(!hashmap.containsKey(p.getName()))
                    hashmap.put(p.getName(), new ArrayList<>());
                // add category to the name
                hashmap.get(p.getName()).add(c.getCategoryName());
            }
        }
        return hashmap;
    }

    private List<String> getListOfMultiCategoryPeople() {
        // find people in more than one category
        List<String> multiCatList = new ArrayList<>();
        for(String name : this.namesHashmapWithListOfCategories.keySet().stream().toList()) {
            if(namesHashmapWithListOfCategories.get(name).size() > 1)
                multiCatList.add(name);
        }
        return multiCatList;
    }

    private List<String> getListOfDuplicates() {
        List<String> duplicates = new ArrayList<>();
        for(String name : multiCategoryPeopleList) {
            for(int i=0; i<namesHashmapWithListOfCategories.get(name).size(); i++ )
                for(int j=i+1; j<namesHashmapWithListOfCategories.get(name).size(); j++ ) {
                    if(namesHashmapWithListOfCategories.get(name).get(i).equals(namesHashmapWithListOfCategories.get(name).get(j)))
                        if(!duplicates.contains(name))
                            duplicates.add(name);
                }
        }
        return duplicates;
    }

}
