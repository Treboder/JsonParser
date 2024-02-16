package com.example.JsonParser.functions;

import com.example.JsonParser.app.JsonParserApplication;
import com.example.JsonParser.objects.Category;
import com.example.JsonParser.objects.DataSet;
import com.example.JsonParser.objects.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Importer {

    private static Logger logger = LoggerFactory.getLogger(JsonParserApplication.class);

    public static DataSet processImportCommand() throws IOException {

        DataSet data = new DataSet();

        // read file
        String fileName = Constants.DATA_OUTPUT_DIR + Constants.DATA_OUTPUT_FILE;
        logger.debug("Read file {}",fileName);
        InputStream inputStream = new FileInputStream(fileName);
        String jsonString = readFromInputStream(inputStream);

        // get array of categories
        JsonArray jsonArray = null;
        try {
            jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
        } catch (Exception e) {
            logger.error("Failed to parse {}", fileName);
            logger.error(e.getMessage());
            return null;
        }

        // parse categories
        for(int i=0; i<jsonArray.size(); i++) {
            JsonObject jsonObject = null;
            try {
                jsonObject = jsonArray.get(i).getAsJsonObject();
                String categoryJSON = jsonObject.toString();
                Category categoryObject = parseCategory(categoryJSON);
                data.addCategory(categoryObject);
                logger.info("Import {} ({})", categoryObject.getCategoryID(), categoryObject.getCategoryName());

            } catch (Exception e) {
                logger.error("Failed to parse {}", fileName);
                logger.error(e.getMessage());
                return null;
            }
        }

        return data;
    }

    public static Category parseCategory(String jsonString) throws IOException {

        JsonObject jsonObject = null;
        try {
            jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (Exception e) {
            logger.error("Failed to parse {}", jsonString);
            logger.error(e.getMessage());
            return null;
        }

        String categoryID = jsonObject.get("categoryID").getAsString();
        String categoryName = jsonObject.get("categoryName").getAsString();
        JsonArray peopleArray = jsonObject.get("people").getAsJsonArray();
        Category category = new Category(categoryID, categoryName);

        for(int i=0; i<peopleArray.size(); i++) {

            // prepare source and target object
            JsonObject personJSON = peopleArray.get(i).getAsJsonObject();
            Person personObject = new Person();

            // parse personJSON
            personObject.setId(parseJsonField(personJSON, "id"));
            personObject.setName(parseJsonField(personJSON, "name"));
            personObject.setBirthYear(parseJsonField(personJSON, "birthYear"));
            personObject.setDeathYear(parseJsonField(personJSON, "deathYear"));
            personObject.setHomeCountry(parseJsonField(personJSON, "homeCountry"));
            personObject.setAchievements(parseJsonField(personJSON, "achievements"));

            // parse keywords
            try {
                JsonArray keywords = personJSON.get("keyWords").getAsJsonArray();
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

    private static String parseJsonField(JsonObject personJSON, String fieldName) {
        String value = "n/a";
        try {
            value = personJSON.get(fieldName).getAsString();
            return value;
        } catch (Exception e) {
            if(fieldName.equals("DeathYear")) {
                logger.debug("Failed to parse field {} from {}", fieldName, personJSON);
                return value;
            }
            else {
                logger.error("Failed to parse field {} from {}", fieldName, personJSON);
                return value;
            }
        }
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

}
