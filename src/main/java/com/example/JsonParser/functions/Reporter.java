package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.example.JsonParser.objects.Category;
import com.example.JsonParser.objects.DataSet;
import com.example.JsonParser.objects.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class Reporter {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

    public static void processGetCommand(String command) {
        // get file argument
        String arg = "";
        if(command.split("").length > 0) {
            arg = command.split(" ")[1];
        }

        // get all categories parsed
        if(arg.equals("all")) {
            for(String categoryID : Analyzer.getInstance().getCategoryHashMap().keySet().stream().toList()) {
                Category c = Analyzer.getInstance().getCategoryHashMap().get(categoryID);
                logger.info("Get {} ({}) with {} elements", categoryID, c.getCategoryName(), c.getPeople().length);
            }
        }
        else {
            // get certain category parsed
            if(Analyzer.getInstance().getCategoryHashMap().containsKey(arg)) {
                Category c = Analyzer.getInstance().getCategoryHashMap().get(arg);
                logger.info("Get {} ({})  ", arg, c.getCategoryName());
                for(Person p : c.getPeople())
                    logger.info("Show {}", p);
            }
            else
                logger.warn("Cant find {}", arg);
        }
    }

    public static void processShowCommand(String command) {
        // get file argument
        String arg = "";
        if(command.split("").length > 0) {
            arg = command.split(" ")[1];
        }

        // get all categories parsed
        if(arg.equals("all")) {
            for(String categoryID : Analyzer.getInstance().getCategoryHashMap().keySet().stream().sorted().toList()) {
                Category c = Analyzer.getInstance().getCategoryHashMap().get(categoryID);
                logger.info("Show {} ({}) with {} people", categoryID, c.getCategoryName(), c.getPeople().length);
                for(Person p : c.getPeople()) {
                    logger.info("------------------------------------------------------");
                    logger.info("Name: {}", p);
                    logger.info("Achievements: {}", p.getAchievements());
                    logger.info("BirthYear: {}", p.getBirthYear());
                    logger.info("DeathYear: {}", p.getDeathYear());
                    logger.info("HomeCountry: {}", p.getHomeCountry());
                    logger.info("KeyWords: {}", Arrays.stream(p.getKeyWords()).toList());
                }
            }
        }

        else if(arg.equals("names")) {
            for(String name : Analyzer.getInstance().getNamesHashmapWithListOfCategories().keySet().stream().sorted().toList())
                logger.info("{} ({})", name, Analyzer.getInstance().getNamesHashmapWithListOfCategories().get(name));
        }

        else if(arg.equals("multi-cat")) {
            List<String> sortedListOfNames = getListOfMultiCategoryPeopleOrderedByNumberOfCategories();
            for(String duplicate : sortedListOfNames)
                for(String category : Analyzer.getInstance().getNamesHashmapWithListOfCategories().get(duplicate))
                    logger.info("{} ({})", duplicate, category);
        }

        else if(arg.equals("duplicates")) {
            logger.info("Found {} duplicates",  Analyzer.getInstance().getDuplicatePeopleList().size());
            for(String duplicate : Analyzer.getInstance().getDuplicatePeopleList())
                for(String category : Analyzer.getInstance().getNamesHashmapWithListOfCategories().get(duplicate))
                    logger.info("{} ({})", duplicate, category);
        }
        else {
            // get certain category parsed
            if(Analyzer.getInstance().getCategoryHashMap().containsKey(arg)) {
                Category c = Analyzer.getInstance().getCategoryHashMap().get(arg);
                logger.info("Show {} ({}) with {} people", arg, c.getCategoryName(), Analyzer.getInstance().getCategoryHashMap().get(arg).getPeople().length);
                for(Person p : c.getPeople()) {
                    logger.info("------------------------------------------------------");
                    logger.info("Name: {}", p);
                    logger.info("Achievements: {}", p.getAchievements());
                    logger.info("BirthYear: {}", p.getBirthYear());
                    logger.info("DeathYear: {}", p.getDeathYear());
                    logger.info("HomeCountry: {}", p.getHomeCountry());
                    logger.info("KeyWords: {}", Arrays.stream(p.getKeyWords()).toList());
                }
            }
            else
                logger.warn("Cant find {}", arg);
        }
    }

    public static void processCountCommand(String command) {
        // get file argument
        String arg = "";
        if(command.split("").length > 0) {
            arg = command.split(" ")[1];
        }

        int names = Analyzer.getInstance().getNamesHashmapWithListOfCategories().keySet().stream().toList().size();
        int categories = Analyzer.getInstance().getCategoryHashMap().keySet().stream().toList().size();
        int multiCat = Analyzer.getInstance().getMultiCategoryPeopleList().size();

        // get all categories parsed
        if(arg.equals("names")) {
            logger.info("Parsed {} distinct names (in {} categories)", names, categories);
        }
        else if(arg.equals("categories")) {
            logger.info("Parsed {} categories with ({} distinct names)", categories, names);
        }
        else if(arg.equals("multi-cat")) {
            logger.info("Analyzed {} people present in more than one category", categories, multiCat);
            List<String> sortedListOfNames = getListOfMultiCategoryPeopleOrderedByNumberOfCategories();
            for(String duplicate : sortedListOfNames)
                logger.info("{} ({})", duplicate, Analyzer.getInstance().getNamesHashmapWithListOfCategories().get(duplicate).size());
        }
        else
            logger.warn("Failed to interpret command");

    }

    private static List<String> getListOfMultiCategoryPeopleOrderedByNumberOfCategories() {

        HashMap<String, Integer> map = new HashMap<>();
        for(String name : Analyzer.getInstance().getMultiCategoryPeopleList())
            map.put(name, Analyzer.getInstance().getNamesHashmapWithListOfCategories().get(name).size());

        Object[] a = map.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        ArrayList<String> sortedNames = new ArrayList<>();
        for (Object e : a) {
            sortedNames.add(Arrays.stream(e.toString().split("=")).toList().get(0));
        }

        Collections.reverse(sortedNames);
        return sortedNames;
    }


}
