package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.example.JsonParser.objects.Category;
import com.example.JsonParser.objects.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class Parser {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

    public static HashMap<String, Category> processParseCommand(String command) {
        // get file argument
        String arg = "";
        if(command.split("").length > 0) {
            arg = command.split(" ")[1];
        }

        HashMap<String, Category> map = new HashMap<>();

        // read all data files
        if(arg.equals("all")) {

            // get list of files
            List<Path> filePaths = null;
            try {
                filePaths = Files.walk(Paths.get(Constants.DATA_INPUT_DIR))
                        .filter(Files::isRegularFile)
                        .toList();

                logger.info("Found {} data files to parse", filePaths.size());
            } catch (IOException e) {
                logger.error("Failed to to fetch data files to parse");
            }

            // go through all files
            if(filePaths != null) {
                for (int i=0; i<filePaths.size(); i++) {
                    String fileName = filePaths.get(i).getFileName().toString();
                    String fileID = fileName.replace(".json", "");
                    // read certain file specified with name
                    try {
                        Category category = parseFileToCategory(fileID);
                        if(category != null) {
                            logger.info("Parsed {} with {} elements", category, category.getPeople().length);
                            map.put(category.getCategoryID(), category);
                        }
                        else
                            logger.error("Failed to parse {}", fileID);

                    } catch (IOException e) {
                        logger.error("Failed to read file");
                    }
                }
            }
        }

        // read certain file specified with argument
        else {
            try {
                Category category = parseFileToCategory(arg);
                if(category != null)
                    logger.info("Parsed {} with {} elements", category, category.getPeople().length);
                else
                    logger.error("Failed to parse {}", arg);
            } catch (IOException e) {
                logger.warn("Failed to read file");
            }
        }

        return map;
    }

    private static Category parseFileToCategory(String catID) throws IOException {

        String fileName = Constants.DATA_INPUT_DIR + catID + ".json";
        logger.debug("Read file {}",fileName);
        InputStream inputStream = new FileInputStream(fileName);
        String jsonString = Utils.readFromInputStream(inputStream);

        logger.debug("Start parsing {}", fileName);

        JsonObject jsonObject = null;
        try {
            jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Failed to parse {}", fileName);
            logger.error(e.getMessage());
            return null;
        }

        String categoryName = jsonObject.get("Category").getAsString();
        JsonArray peopleArray = jsonObject.get("Results").getAsJsonArray();
        Category category = new Category(catID, categoryName);

        for(int i=0; i<peopleArray.size(); i++) {

            // prepare source and target object
            JsonObject personJSON = peopleArray.get(i).getAsJsonObject();
            Person personObject = new Person();

            // parse personJSON
            personObject.setId(Utils.parseJsonField(personJSON, "Number"));
            personObject.setName(Utils.parseJsonField(personJSON, "Name"));
            personObject.setBirthYear(Utils.parseJsonField(personJSON, "BirthYear"));
            personObject.setDeathYear(Utils.parseJsonField(personJSON, "DeathYear"));
            personObject.setHomeCountry(Utils.parseJsonField(personJSON, "HomeCountry"));
            personObject.setAchievements(Utils.parseJsonField(personJSON, "Achievements"));

            // parse keywords
            try {
                JsonArray keywords = personJSON.get("Keywords").getAsJsonArray();
                personObject.setKeyWords(new String[keywords.size()]);
                for(int j=0; j< personObject.getKeyWords().length; j++) {
                    personObject.getKeyWords()[j] = keywords.get(j).getAsString();
                }
            } catch (Exception e) {
                logger.error("Failed to parse keywords from {}", personJSON);
            }

            // add personJSON to category
            category.getPeople()[i] = personObject;
            logger.debug("Parse {}", personObject);
        }
        return category;
    }



}
