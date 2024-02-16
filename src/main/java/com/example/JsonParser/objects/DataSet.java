package com.example.JsonParser.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSet {

    private List<Category> categories = new ArrayList<>();

    public DataSet() {

    }

    public DataSet(HashMap<String, Category> categoryHashMap) {
        this.categories = new ArrayList<>();
        for(String key : categoryHashMap.keySet().stream().sorted().toList()) {
            this.categories.add(categoryHashMap.get(key));
        }
    }

    public void addCategory(Category c) {
        this.categories.add(c);
    }

    public String toJSON() {
        Gson gson = new Gson();
        String uglyJsonString = gson.toJson(categories);
        gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJsonString);
        String prettyJsonString = gson.toJson(jsonElement);
        return prettyJsonString;
    }

    public HashMap<String, Category> toHashMap() {
        HashMap<String, Category> categoryHashMap = new HashMap<>();
        for(Category c : this.categories)
            categoryHashMap.put(c.getCategoryID(), c);

        return categoryHashMap;
    }





}
